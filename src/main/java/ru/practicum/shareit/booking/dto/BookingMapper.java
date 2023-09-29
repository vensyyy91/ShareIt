package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        long id = booking.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        ItemDto item = ItemMapper.toItemDto(booking.getItem());
        UserDto booker = UserMapper.toUserDto(booking.getBooker());
        Status status = booking.getStatus();

        return new BookingDto(id, start, end, item, booker, status);
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        long id = booking.getId();
        long bookerId = booking.getBooker().getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        return new BookingInfoDto(id, bookerId, start, end);
    }

    public static Booking toBooking(BookingCreationDto bookingCreationDto) {
        LocalDateTime start = bookingCreationDto.getStart();
        LocalDateTime end = bookingCreationDto.getEnd();

        return new Booking(0, start, end, null, null, null);
    }
}