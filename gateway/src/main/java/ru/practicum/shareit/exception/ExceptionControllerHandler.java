package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.StringJoiner;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringJoiner joiner = new StringJoiner("; ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ex.getBindingResult().getErrorCount(); i++) {
            ObjectError error = ex.getBindingResult().getAllErrors().get(i);
            if (error instanceof FieldError) {
                String field = ((FieldError)error).getField();
                builder.append("Неверно заполнено поле ").append(field).append(": ");
            }
            builder.append(error.getDefaultMessage());
            joiner.add(builder.toString());
            builder.setLength(0);
        }
        String message = joiner.toString();
        log.error("Получен запрос с некорректными данными.");
        log.error(message);

        return new Response(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        StringJoiner joiner = new StringJoiner("; ");
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            String violationMessage = cv.getMessage();
            String[] path = cv.getPropertyPath().toString().split("\\.");
            String param = path[1];
            String errorMessage = (String.format("Неверно указан параметр %s: %s", param, violationMessage));
            joiner.add(errorMessage);
        }
        String message = joiner.toString();
        log.error("Получен запрос с некорректными данными.");
        log.error(message);

        return new Response(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        String message = ex.getMessage();
        if (ex.getHeaderName().equals("X-Sharer-User-Id")) {
            message = "Не указан идентификатор пользователя.";
        }
        log.error(message);

        return new Response(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleIllegalArgumentException(IllegalArgumentException ex) {
        String message = ex.getMessage();
        log.error(message);

        return new Response(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = "Не указан обязательный параметр: " + ex.getParameterName();
        log.error(message);

        return new Response(message);
    }
}