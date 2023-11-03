package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        Item item = booking.getItem();
        User booker = booking.getBooker();

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(new BookingDto.Item(item.getId(), item.getName()))
                .booker(new BookingDto.Booker(booker.getId(), booker.getName()))
                .status(booking.getStatus())
                .build();
    }

    public BookingInfoDto toBookingInfoDto(Booking booking) {
        Long id = booking.getId();
        Long bookerId = booking.getBooker().getId();
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        return new BookingInfoDto(id, bookerId, start, end);
    }

    public Booking toBooking(BookingCreationDto bookingCreationDto) {
        return Booking.builder()
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .build();
    }
}