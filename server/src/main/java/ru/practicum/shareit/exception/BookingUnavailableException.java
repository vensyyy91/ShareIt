package ru.practicum.shareit.exception;

public class BookingUnavailableException extends RuntimeException {
    public BookingUnavailableException(String message) {
        super(message);
    }
}