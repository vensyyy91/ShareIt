package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER) @Positive long userId,
												  @RequestParam(name = "state", defaultValue = "all") String stateParam,
												  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
												  @RequestParam(defaultValue = "10") @Positive int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Получен запрос GET /bookings?state={}&from={}&size={}", state, from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getUserItemsBookings(@RequestHeader(USER_ID_HEADER) @Positive long userId,
													   @RequestParam(name = "state", defaultValue = "all") String stateParam,
													   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
													   @RequestParam(defaultValue = "10") @Positive int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Получен запрос GET /bookings/owner?state={}&from={}&size={}", state, from, size);
		return bookingClient.getUserItemsBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID_HEADER) @Positive long userId,
											 @RequestBody @Valid BookingDto bookingDto) {
		log.info("Получен запрос POST /bookings");
		return bookingClient.addBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) @Positive long userId,
									 @PathVariable @Positive long bookingId,
									 @RequestParam boolean approved) {
		log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) @Positive long userId,
											 @PathVariable @Positive long bookingId) {
		log.info("Получен запрос GET /bookings/{}", bookingId);
		return bookingClient.getBooking(userId, bookingId);
	}
}