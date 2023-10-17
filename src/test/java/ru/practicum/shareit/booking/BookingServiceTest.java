package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private BookingDto bookingDto1;
    private BookingCreationDto bookingCreationDto1;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "user1", "user1@test.com");
        user2 = new User(2L, "user2", "user2@test.com");
        user3 = new User(3L, "user3", "user3@test.com");
        item1 = new Item(1L, "item1", "first item for test", 3L, true, 0L);
        item2 = new Item(2L, "item2", "second item for test", 3L, false, 0L);
        booking1 = new Booking(
                1L,
                LocalDateTime.now().plusMinutes(1),
                LocalDateTime.now().plusMinutes(5),
                item1,
                user1,
                Status.WAITING
        );
        bookingDto1 = BookingMapper.toBookingDto(booking1);
        bookingCreationDto1 = new BookingCreationDto(
                1L,
                booking1.getStart(),
                booking1.getEnd()
        );
    }

    @Test
    public void addBookingToAvailableItem_shouldReturnBooking() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocationOnMock -> {
                    Booking booking = invocationOnMock.getArgument(0, Booking.class);
                    booking.setId(1L);
                    return booking;
                });


        BookingDto booking = bookingService.addBooking(user1.getId(), bookingCreationDto1);

        assertEquals(bookingDto1, booking);
    }

    @Test
    public void addBookingFromNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.addBooking(99, bookingCreationDto1));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void addBookingToNotExistingItem_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());
        bookingCreationDto1.setItemId(99L);

        Throwable ex = assertThrows(ItemNotFoundException.class,
                () -> bookingService.addBooking(user1.getId(), bookingCreationDto1));
        assertEquals("Вещь с id=99 не найдена.", ex.getMessage());
    }

    @Test
    public void addBookingByItemOwner_shouldThrowException() {
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        when(itemRepository.findById(item1.getId()))
                .thenReturn(Optional.ofNullable(item1));

        Throwable ex = assertThrows(AccessDeniedException.class,
                () -> bookingService.addBooking(user3.getId(), bookingCreationDto1));
        assertEquals("Владелец не может забронировать собственную вещь.", ex.getMessage());
    }

    @Test
    public void addBookingToUnavailableItem_shouldThrowException() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(item2.getId()))
                .thenReturn(Optional.ofNullable(item2));
        Booking booking2 = new Booking(
                2L,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(1),
                item2,
                user1,
                Status.WAITING
        );
        BookingCreationDto bookingCreationDto2 = new BookingCreationDto(
                2L,
                booking2.getStart(),
                booking2.getEnd()
        );

        Throwable ex = assertThrows(ItemUnavailableException.class,
                () -> bookingService.addBooking(user1.getId(), bookingCreationDto2));
        assertEquals("Вещь с id=2 недоступна.", ex.getMessage());
    }

    @Test
    public void approveBookingAcceptByOwner_shouldReturnAcceptedBooking() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));

        BookingDto booking = bookingService.approveBooking(user3.getId(), booking1.getId(), true);

        assertEquals(bookingDto1.getId(), booking.getId());
        assertEquals(bookingDto1.getStart(), booking.getStart());
        assertEquals(bookingDto1.getEnd(), booking.getEnd());
        assertEquals(bookingDto1.getItem(), booking.getItem());
        assertEquals(bookingDto1.getBooker(), booking.getBooker());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    public void approveBookingRejectByOwner_shouldReturnRejectedBooking() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));

        BookingDto booking = bookingService.approveBooking(user3.getId(), booking1.getId(), false);

        assertEquals(bookingDto1.getId(), booking.getId());
        assertEquals(bookingDto1.getStart(), booking.getStart());
        assertEquals(bookingDto1.getEnd(), booking.getEnd());
        assertEquals(bookingDto1.getItem(), booking.getItem());
        assertEquals(bookingDto1.getBooker(), booking.getBooker());
        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    public void approveBookingByAnotherUser_shouldThrowException() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));

        Throwable ex = assertThrows(AccessDeniedException.class,
                () -> bookingService.approveBooking(user1.getId(), booking1.getId(), true));
        assertEquals("Подтверждать или отклонять бронирование может только владелец вещи.", ex.getMessage());
    }

    @Test
    public void approveBookingByNotExistingUser_shouldThrowException() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.approveBooking(99, booking1.getId(), true));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void approveBookingAlreadyApproved_shouldThrowException() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));
        booking1.setStatus(Status.APPROVED);

        Throwable ex = assertThrows(BookingUnavailableException.class,
                () -> bookingService.approveBooking(user3.getId(), booking1.getId(), true));
        assertEquals("Бронирование с id=1 уже было подтверждено или отклонено ранее.", ex.getMessage());
    }

    @Test
    public void approveBookingByNotExistingId_shouldThrowException() {
        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(user3.getId(), 99, true));
        assertEquals("Бронирование с id=99 не найдено.", ex.getMessage());
    }

    @Test
    public void getBookingByItemOwner_shouldReturnBooking() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user3.getId()))
                .thenReturn(Optional.ofNullable(user3));

        BookingDto booking = bookingService.getBooking(user3.getId(), booking1.getId());

        assertEquals(bookingDto1, booking);
    }

    @Test
    public void getBookingByBooker_shouldReturnBooking() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDto booking = bookingService.getBooking(user1.getId(), booking1.getId());

        assertEquals(bookingDto1, booking);
    }

    @Test
    public void getBookingByAnotherUser_shouldThrowException() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.ofNullable(user2));

        Throwable ex = assertThrows(AccessDeniedException.class,
                () -> bookingService.getBooking(user2.getId(), booking1.getId()));
        assertEquals("Получить информацию о бронировании может только владелец вещи или автор бронирования.",
                ex.getMessage());
    }

    @Test
    public void getBookingByNotExistingUser_shouldThrowException() {
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getBooking(99, booking1.getId()));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void getBookingByNotExistingId_shouldThrowException() {
        when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(user1.getId(), 99));
        assertEquals("Бронирование с id=99 не найдено.", ex.getMessage());
    }

    @Test
    public void getUserBookings_withStateAll_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerId(
                user1.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"))
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.ALL, 0, 10);

        verify(bookingRepository).findAllByBookerId(user1.getId(),
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")));
    }

    @Test
    public void getUserBookings_withStatePast_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndEndBefore(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.PAST, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndEndBefore(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserBookings_withStateCurrent_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllCurrentByBookerId(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.CURRENT, 0, 10);

        verify(bookingRepository).findAllCurrentByBookerId(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserBookings_withStateFuture_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStartAfter(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.FUTURE, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndStartAfter(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserBookings_withStateWaiting_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStatus(
                eq(user1.getId()),
                eq(Status.WAITING),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.WAITING, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndStatus(
                eq(user1.getId()),
                eq(Status.WAITING),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserBookings_withStateRejected_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdAndStatus(
                eq(user1.getId()),
                eq(Status.REJECTED),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserBookings(user1.getId(), State.REJECTED, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndStatus(
                eq(user1.getId()),
                eq(Status.REJECTED),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserBookings_withNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getUserBookings(99, State.ALL, 0, 10));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void getUserItemsBookings_withStateAll_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwner(
                eq(user1.getId()),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.ALL, 0, 10);

        verify(bookingRepository).findAllByItemOwner(eq(user1.getId()), any(PageRequest.class));
    }

    @Test
    public void getUserItemsBookings_withStatePast_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwnerAndEndBefore(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.PAST, 0, 10);

        verify(bookingRepository).findAllByItemOwnerAndEndBefore(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserItemsBookings_withStateCurrent_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllCurrentByItemOwner(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.CURRENT, 0, 10);

        verify(bookingRepository).findAllCurrentByItemOwner(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserItemsBookings_withStateFuture_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwnerAndStartAfter(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.FUTURE, 0, 10);

        verify(bookingRepository).findAllByItemOwnerAndStartAfter(
                eq(user1.getId()),
                any(LocalDateTime.class),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserItemsBookings_withStateWaiting_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwnerAndStatus(
                eq(user1.getId()),
                eq(Status.WAITING),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.WAITING, 0, 10);

        verify(bookingRepository).findAllByItemOwnerAndStatus(
                eq(user1.getId()),
                eq(Status.WAITING),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserItemsBookings_withStateRejected_shouldInvokeCorrespondingRepositoryMethod() {
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByItemOwnerAndStatus(
                eq(user1.getId()),
                eq(Status.REJECTED),
                any(PageRequest.class)
        ))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        bookingService.getUserItemsBookings(user1.getId(), State.REJECTED, 0, 10);

        verify(bookingRepository).findAllByItemOwnerAndStatus(
                eq(user1.getId()),
                eq(Status.REJECTED),
                any(PageRequest.class)
        );
    }

    @Test
    public void getUserItemsBookings_withNotExistingUser_shouldThrowException() {
        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class,
                () -> bookingService.getUserItemsBookings(99, State.ALL, 0, 10));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }
}