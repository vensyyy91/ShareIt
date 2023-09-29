package ru.practicum.shareit.annotations;

import ru.practicum.shareit.validation.BookingValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BookingValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {
    String message() default "Время окончания бронирования должно быть позже времени начала бронирования.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}