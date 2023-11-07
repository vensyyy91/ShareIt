package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemInfoDto itemInfoDto1;
    private ItemInfoDto itemInfoDto2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private Comment comment4;
    private CommentDto commentDto1;
    private CommentDto commentDto2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "user1", "user1@test.com");
        user2 = new User(2L, "user2", "user2@test.com");
        user3 = new User(3L, "user3", "user3@test.com");
        item1 = new Item(1L, "item1", "first item for test", 3L, true, 0L);
        item2 = new Item(2L, "item2", "second item for test", 3L, true, 0L);
        itemDto1 = ItemMapper.toItemDto(item1);
        itemDto2 = ItemMapper.toItemDto(item2);
        booking1 = new Booking(
                1L,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                item1,
                user1,
                Status.APPROVED
        );
        booking2 = new Booking(
                2L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10),
                item1,
                user2,
                Status.APPROVED
        );
        booking3 = new Booking(
                3L,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                item2,
                user1,
                Status.APPROVED
        );
        booking4 = new Booking(
                4L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10),
                item2,
                user2,
                Status.APPROVED
        );
        comment1 = new Comment(1L, "first comment for test", item1, user1, LocalDateTime.now());
        comment2 = new Comment(2L, "second comment for test", item1, user2, LocalDateTime.now().plusMinutes(1));
        comment3 = new Comment(3L, "third comment for test", item2, user1, LocalDateTime.now().plusMinutes(2));
        comment4 = new Comment(4L, "fourth comment for test", item2, user2, LocalDateTime.now().plusMinutes(3));
        commentDto1 = CommentMapper.toCommentDto(comment1);
        commentDto2 = CommentMapper.toCommentDto(comment2);
        CommentDto commentDto3 = CommentMapper.toCommentDto(comment3);
        CommentDto commentDto4 = CommentMapper.toCommentDto(comment4);
        itemInfoDto1 = ItemMapper.toItemInfoDto(
                item1,
                BookingMapper.toBookingInfoDto(booking1),
                BookingMapper.toBookingInfoDto(booking2),
                List.of(commentDto1, commentDto2)
        );
        itemInfoDto2 = ItemMapper.toItemInfoDto(
                item2,
                BookingMapper.toBookingInfoDto(booking3),
                BookingMapper.toBookingInfoDto(booking4),
                List.of(commentDto3, commentDto4)
        );
    }

    @Test
    public void getAllItems_whenHaveItems_shouldReturnList() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findAllByOwnerOrderById(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));
        when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(comment1, comment2));
        when(commentRepository.findAllByItemId(item2.getId()))
                .thenReturn(List.of(comment3, comment4));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));
        when(bookingRepository.findAllByItemId(item2.getId()))
                .thenReturn(List.of(booking3, booking4));

        List<ItemInfoDto> expected = List.of(itemInfoDto1, itemInfoDto2);
        List<ItemInfoDto> items = itemService.getAllItems(user1.getId(), 0, 10);

        assertIterableEquals(expected, items);
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void getAllItems_whenEmpty_shouldReturnEmptyList() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findAllByOwnerOrderById(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemInfoDto> items = itemService.getAllItems(user1.getId(), 0, 10);

        assertEquals(0, items.size());
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void getAllItems_byNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class, () -> itemService.getAllItems(99, 0, 10));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void getItemById_withExistingIdFromOwner_shouldReturnItem() {
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        ItemInfoDto item = itemService.getItemById(user3.getId(), item1.getId());

        assertEquals(itemInfoDto1, item);
        verify(userRepository, times(1)).findById(user3.getId());
    }

    @Test
    public void getItemById_withExistingIdFromAnotherUser_shouldReturnItemWithoutBookings() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));
        itemInfoDto1.setLastBooking(null);
        itemInfoDto1.setNextBooking(null);

        ItemInfoDto item = itemService.getItemById(user1.getId(), item1.getId());

        assertEquals(itemInfoDto1, item);
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void getItemById_withNoBookings_shouldReturnItemWithoutBookings() {
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(comment1, comment2));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());
        itemInfoDto1.setLastBooking(null);
        itemInfoDto1.setNextBooking(null);

        ItemInfoDto item = itemService.getItemById(user3.getId(), item1.getId());

        assertEquals(itemInfoDto1, item);
        verify(userRepository, times(1)).findById(user3.getId());
    }

    @Test
    public void getItemById_withNoComments_shouldReturnItemWithoutComments() {
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(commentRepository.findAllByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));
        itemInfoDto1.setComments(new ArrayList<>());

        ItemInfoDto item = itemService.getItemById(user3.getId(), item1.getId());

        assertEquals(itemInfoDto1, item);
        verify(userRepository, times(1)).findById(user3.getId());
    }

    @Test
    public void getItemById_withExistingIdFromNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class, () -> itemService.getItemById(99, item1.getId()));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void getItemById_withNotExistingId_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(user1.getId(), 99));
        assertEquals("Вещь с id=99 не найдена.", ex.getMessage());
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void addItem_fromExistingUser_shouldReturnItem() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(item1))
                .thenReturn(item1);

        ItemDto item = itemService.addItem(user1.getId(), itemDto1);

        assertEquals(itemDto1, item);
        verify(userRepository, times(1)).findById(user1.getId());
    }

    @Test
    public void addItem_fromNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class, () -> itemService.addItem(99, itemDto1));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void updateItem_byOwner_shouldReturnItem() {
        ItemDto itemDtoForUpdate = new ItemDto(0L, "updatedItem", "updated item for test", false, 0L);
        Item updatedItem = ItemMapper.toItem(itemDtoForUpdate);
        updatedItem.setId(item1.getId());
        updatedItem.setOwner(user3.getId());
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemDto updatedItemDto = itemService.updateItem(user3.getId(), item1.getId(), itemDtoForUpdate);

        assertEquals(1L, updatedItemDto.getId());
        assertEquals("updatedItem", updatedItemDto.getName());
        assertEquals("updated item for test", updatedItemDto.getDescription());
        assertFalse(updatedItemDto.getAvailable());
    }

    @Test
    public void updateItem_byAnotherUser_shouldThrowException() {
        ItemDto itemDtoForUpdate = new ItemDto(0L, "updatedItem", "updated item for test", false, 0L);
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        Throwable ex = assertThrows(AccessDeniedException.class,
                () -> itemService.updateItem(user1.getId(), item1.getId(), itemDtoForUpdate));
        assertEquals("Редактировать вещь может только её владелец.", ex.getMessage());
    }

    @Test
    public void updateItem_onlyWithName_shouldReturnItemWithOnlyNameUpdated() {
        ItemDto itemDtoForUpdate = new ItemDto(0L, "updatedItem", null, null, 0L);
        Item updatedItem = ItemMapper.toItem(itemDtoForUpdate);
        updatedItem.setId(item1.getId());
        updatedItem.setOwner(user3.getId());
        updatedItem.setDescription("first item for test");
        updatedItem.setAvailable(true);
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemDto updatedItemDto = itemService.updateItem(user3.getId(), item1.getId(), itemDtoForUpdate);

        assertEquals(1L, updatedItemDto.getId());
        assertEquals("updatedItem", updatedItemDto.getName());
        assertEquals("first item for test", updatedItemDto.getDescription());
        assertTrue(updatedItemDto.getAvailable());
    }

    @Test
    public void updateItem_onlyWithDescription_shouldReturnItemWithOnlyDescriptionUpdated() {
        ItemDto itemDtoForUpdate = new ItemDto(0L, null, "updated item for test", null, 0L);
        Item updatedItem = ItemMapper.toItem(itemDtoForUpdate);
        updatedItem.setId(item1.getId());
        updatedItem.setOwner(user3.getId());
        updatedItem.setName("item1");
        updatedItem.setAvailable(true);
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemDto updatedItemDto = itemService.updateItem(user3.getId(), item1.getId(), itemDtoForUpdate);

        assertEquals(1L, updatedItemDto.getId());
        assertEquals("item1", updatedItemDto.getName());
        assertEquals("updated item for test", updatedItemDto.getDescription());
        assertTrue(updatedItemDto.getAvailable());
    }

    @Test
    public void updateItem_onlyWithAvailable_shouldReturnItemWithOnlyAvailableUpdated() {
        ItemDto itemDtoForUpdate = new ItemDto(0L, null, null, false, 0L);
        Item updatedItem = ItemMapper.toItem(itemDtoForUpdate);
        updatedItem.setId(item1.getId());
        updatedItem.setOwner(user3.getId());
        updatedItem.setName("item1");
        updatedItem.setDescription("first item for test");
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemDto updatedItemDto = itemService.updateItem(user3.getId(), item1.getId(), itemDtoForUpdate);

        assertEquals(1L, updatedItemDto.getId());
        assertEquals("item1", updatedItemDto.getName());
        assertEquals("first item for test", updatedItemDto.getDescription());
        assertFalse(updatedItemDto.getAvailable());
    }

    @Test
    public void searchItem_withText_shouldReturnList() {
        when(itemRepository.search("item", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        List<ItemDto> expected = List.of(itemDto1, itemDto2);
        List<ItemDto> items = itemService.searchItem("item", 0,10);

        assertEquals(2, items.size());
        assertIterableEquals(expected, items);
    }

    @Test
    public void searchItem_withEmptyString_shouldReturnEmptyList() {
        List<ItemDto> items = itemService.searchItem("", 0,10);

        assertEquals(0, items.size());
    }

    @Test
    public void addComment_fromUserBookedInPast_shouldReturnComment() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));
        when(commentRepository.save(comment1))
                .thenReturn(comment1);

        CommentDto comment = itemService.addComment(user1.getId(), item1.getId(), commentDto1);

        assertEquals(commentDto1, comment);
    }

    @Test
    public void addComment_fromUserBookingNow_shouldThrowException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        booking2.setStart(LocalDateTime.now().minusMinutes(1));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(user2.getId(), item1.getId(), commentDto2));
        assertEquals("Пользователь с id=2 не брал в аренду вещь с id=1", ex.getMessage());
    }

    @Test
    public void addComment_fromUserWillBookInFuture_shouldThrowException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(user2.getId(), item1.getId(), commentDto2));
        assertEquals("Пользователь с id=2 не брал в аренду вещь с id=1", ex.getMessage());
    }

    @Test
    public void addComment_fromUserWithBookingHavingWaitingStatus_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        booking1.setStatus(Status.WAITING);
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(user1.getId(), item1.getId(), commentDto1));
        assertEquals("Пользователь с id=1 не брал в аренду вещь с id=1", ex.getMessage());
    }

    @Test
    public void addComment_fromUserWithBookingHavingRejectedStatus_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        booking1.setStatus(Status.REJECTED);
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(user1.getId(), item1.getId(), commentDto1));
        assertEquals("Пользователь с id=1 не брал в аренду вещь с id=1", ex.getMessage());
    }

    @Test
    public void addComment_fromAnotherUser_shouldThrowException() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.findAllByItemId(item1.getId()))
                .thenReturn(List.of(booking1, booking2));

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> itemService.addComment(user2.getId(), item1.getId(), commentDto1));
        assertEquals(String.format("Пользователь с id=%d не брал в аренду вещь с id=%d", user2.getId(), item1.getId()),
                ex.getMessage());
    }
}