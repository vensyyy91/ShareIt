package ru.practicum.shareit.validation;

import ru.practicum.shareit.annotations.StartBeforeEnd;
import ru.practicum.shareit.booking.dto.BookingCreationDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingValidator implements ConstraintValidator<StartBeforeEnd, BookingCreationDto> {

    @Override
    public void initialize(StartBeforeEnd constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingCreationDto booking, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();
        if (start == null || end == null) {
            return true;
        }

        return start.isBefore(end);
    }
}