package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public List<String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<String> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            log.warn("MethodArgumentNotValidException: Validation failed " +
                     "for value [{}] in field {}.{}; message: {}",
                     fieldError.getRejectedValue(), fieldError.getObjectName(),
                     fieldError.getField(), fieldError.getDefaultMessage());
        }
        return fieldErrors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(FilmNotFoundException e) {
        return new ErrorResponse("FilmNotFound", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMpaNotFoundException(MpaNotFoundException e) {
        return new ErrorResponse("MpaNotFound", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse("UserNotFound", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleEmailAlreadyTakenException(EmailAlreadyTakenException e) {
        return new ErrorResponse("EmailAlreadyTaken", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleLoginAlreadyTakenException(LoginAlreadyTakenException e) {
        return new ErrorResponse("LoginAlreadyTaken", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidationException(ValidationException e) {
        return new ErrorResponse("InvalidObject", e.getMessage());
    }
}

class ErrorResponse {
    String error;
    String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
