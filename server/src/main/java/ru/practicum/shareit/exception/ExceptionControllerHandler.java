package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerHandler {
    @ExceptionHandler({ItemUnavailableException.class, BookingUnavailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleUnavailableException(RuntimeException ex) {
        log.error(ex.getMessage());

        return new Response(ex.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class,
            ItemNotFoundException.class,
            BookingNotFoundException.class,
            RequestNotFoundException.class,
            AccessDeniedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response handleNotFoundException(RuntimeException ex) {
        log.error(ex.getMessage());

        return new Response((ex.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response handlePSQLException(PSQLException ex) {
        String message;
        ServerErrorMessage sem = ex.getServerErrorMessage();
        if (sem != null) {
            message = sem.getDetail();
        } else {
            message = ex.getMessage();
        }
        log.error(message);

        return new Response(message);
    }
}