package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequestDto itemRequestDto1;
    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "user1", "user1@test.com");
        user2 = new User(2L, "user2", "user2@test.com");
        item1 = new Item(1L, "item1", "first item for test", 2L, true, 1L);
        item2 = new Item(2L, "item2", "second item for test", 2L, true, 2L);
        itemDto1 = ItemMapper.toItemDto(item1);
        itemDto2 = ItemMapper.toItemDto(item2);
        itemRequest1 = new ItemRequest(
                1L,
                "first test request description",
                1L,
                LocalDateTime.now()
        );
        itemRequest2 = new ItemRequest(
                2L,
                "second test request description",
                1L,
                LocalDateTime.now().minusMinutes(10)
        );
        itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);
    }

    @Test
    void addRequest_ShouldReturnRequest() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.save(itemRequest1))
                .thenReturn(itemRequest1);
        ItemRequestDto itemRequestDtoNew = new ItemRequestDto(
                1L,
                "test request description",
                0L,
                null,
                null
        );

        ItemRequestDto itemRequest = itemRequestService.addRequest(user1.getId(), itemRequestDtoNew);

        assertEquals(itemRequestDto1, itemRequest);
    }

    @Test
    void addRequest_withNotExistingUser_ShouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());
        ItemRequestDto itemRequestDtoNew = new ItemRequestDto(
                1L,
                "test request description",
                0L,
                null,
                null
        );

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.addRequest(99, itemRequestDtoNew));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    void getUserRequests_whenUserHaveRequests_shouldReturnList() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findAllByRequester(user1.getId()))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        List<ItemRequestDto> expected = List.of(itemRequestDto1, itemRequestDto2);
        List<ItemRequestDto> actual = itemRequestService.getUserRequests(user1.getId());

        assertIterableEquals(expected, actual);
    }

    @Test
    void getUserRequests_whenEmpty_shouldReturnEmptyList() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findAllByRequester(user1.getId()))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> requests = itemRequestService.getUserRequests(user1.getId());

        assertEquals(0, requests.size());
    }

    @Test
    void getUserRequests_withNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getUserRequests(99));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    void getAllRequests_shouldReturnList() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        itemRequestDto1.getItems().add(itemDto1);
        itemRequestDto2.getItems().add(itemDto2);
        when(itemRepository.findAllByRequest(itemRequest1.getId()))
                .thenReturn(List.of(item1));
        when(itemRepository.findAllByRequest(itemRequest2.getId()))
                .thenReturn(List.of(item2));
        when(itemRequestRepository.findAllByRequesterIsNot(
                user2.getId(),
                PageRequest.of(0, 10, Sort.by("created").descending())
        ))
                .thenReturn(new PageImpl<>(List.of(itemRequest1, itemRequest2)));

        List<ItemRequestDto> expected = List.of(itemRequestDto1, itemRequestDto2);
        List<ItemRequestDto> actual = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertIterableEquals(expected, actual);
    }

    @Test
    void getAllRequests_whenEmpty_shouldReturnEmptyList() {
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRequestRepository.findAllByRequesterIsNot(
                user2.getId(),
                PageRequest.of(0, 10, Sort.by("created").descending())
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> requests = itemRequestService.getAllRequests(user2.getId(), 0, 10);

        assertEquals(0, requests.size());
    }

    @Test
    void getAllRequests_withNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllRequests(99, 0 , 10));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    void getRequestById_withExistingId_shouldReturnRequest() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(itemRequest1.getId()))
                .thenReturn(Optional.ofNullable(itemRequest1));
        when(itemRepository.findAllByRequest(itemRequest1.getId()))
                .thenReturn(List.of(item1));
        itemRequestDto1.getItems().add(itemDto1);

        ItemRequestDto request = itemRequestService.getRequestById(user1.getId(), itemRequest1.getId());

        assertEquals(itemRequestDto1, request);
    }

    @Test
    void getRequestById_withNoItems_shouldReturnRequestWithEmptyItemList() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(itemRequest1.getId()))
                .thenReturn(Optional.ofNullable(itemRequest1));
        when(itemRepository.findAllByRequest(itemRequest1.getId()))
                .thenReturn(new ArrayList<>());

        ItemRequestDto request = itemRequestService.getRequestById(user1.getId(), itemRequest1.getId());

        assertEquals(itemRequestDto1, request);
    }

    @Test
    void getRequestById_withNotExistingUserId_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getRequestById(99, itemRequest1.getId()));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    void getRequestById_withNotExistingRequestId_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRequestRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(RequestNotFoundException.class,
                () -> itemRequestService.getRequestById(user1.getId(), 99));
        assertEquals("Запрос с id=99 не найден.", ex.getMessage());
    }
}