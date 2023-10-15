package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto addBooking(long userId, BookingCreationDto bookingCreationDto) {
        User booker = getUser(userId);
        Item item = getItem(bookingCreationDto.getItemId());
        if (Objects.equals(item.getOwner(), booker.getId())) {
            throw new AccessDeniedException("Владелец не может забронировать собственную вещь.");
        }
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Вещь с id=" + bookingCreationDto.getItemId() + " недоступна.");
        }
        Booking booking = BookingMapper.toBooking(bookingCreationDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info(String.format("Пользователем с id=%d добавлено бронирование: %s", userId, newBooking));

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);
        User owner = getUser(userId);
        if (!Objects.equals(booking.getItem().getOwner(), owner.getId())) {
            throw new AccessDeniedException("Подтверждать или отклонять бронирование может только владелец вещи.");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingUnavailableException("Бронирование с id=" + bookingId + " уже было подтверждено или отклонено ранее.");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        log.info(String.format("Владелец вещи изменил статус бронирования с id=%d на %s", bookingId, booking.getStatus()));

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        User user = getUser(userId);
        if (!Objects.equals(booking.getItem().getOwner(), user.getId()) && !Objects.equals(booking.getBooker().getId(), user.getId())) {
            throw new AccessDeniedException("Получить информацию о бронировании может только владелец вещи или автор бронирования.");
        }
        log.info("Возвращено бронирование: " + booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, State state, int from, int size) {
        getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(
                        userId,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByBookerId(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
        }
        log.info(String.format("Возвращен список всех бронирований пользователя с id=%d, параметр state=%s: %s",
                userId, state, bookings));

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getUserItemsBookings(long userId, State state, int from, int size) {
        getUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerOrderByStartDesc(
                        userId,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentByItemOwner(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(from / size, size)
                ).getContent();
                break;
        }
        log.info(String.format("Возвращен список бронирований для всех вещей пользователя с id=%d, параметр state=%s: %s",
                userId, state, bookings));

        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден."));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    private Booking getBooking(long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование с id=" + bookingId + " не найдено."));
    }
}