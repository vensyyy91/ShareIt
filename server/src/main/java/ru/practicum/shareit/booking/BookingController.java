package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                 @RequestBody BookingCreationDto bookingCreationDtoDto) {
        log.info("Получен запрос POST /bookings");
        return bookingService.addBooking(userId, bookingCreationDtoDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) long userId,
                               @PathVariable long bookingId,
                               @RequestParam boolean approved) {
        log.info("Получен запрос PATCH /bookings/{}", bookingId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long bookingId) {
        log.info("Получен запрос GET /bookings/{}", bookingId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                            @RequestParam State state,
                                            @RequestParam int from,
                                            @RequestParam int size) {
        log.info("Получен запрос GET /bookings?state={}&from={}&size={}", state, from, size);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getUserItemsBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "ALL") State state,
                                                 @RequestParam int from,
                                                 @RequestParam int size) {
        log.info("Получен запрос GET /bookings/owner?state={}&from={}&size={}", state, from, size);
        return bookingService.getUserItemsBookings(userId, state, from, size);
    }
}