package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.annotations.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@StartBeforeEnd
public class BookingDto {
	@NotNull
	private Long itemId;
	@FutureOrPresent
	@NotNull
	private LocalDateTime start;
	@Future
	@NotNull
	private LocalDateTime end;
}