package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        Long id = booking.getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        Long itemId = booking.getItem().getId();
        String itemName = booking.getItem().getName();
        BookingDto.Item item = new BookingDto.Item(itemId, itemName);
        Long bookerId = booking.getBooker().getId();
        String bookerName = booking.getBooker().getName();
        BookingDto.Booker booker = new BookingDto.Booker(bookerId, bookerName);
        Status status = booking.getStatus();

        return new BookingDto(id, start, end, item, booker, status);
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        Long id = booking.getId();
        Long bookerId = booking.getBooker().getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        return new BookingInfoDto(id, bookerId, start, end);
    }

    public static Booking toBooking(BookingCreationDto bookingCreationDto) {
        LocalDateTime start = bookingCreationDto.getStart();
        LocalDateTime end = bookingCreationDto.getEnd();

        return new Booking(null, start, end, null, null, null);
    }
}