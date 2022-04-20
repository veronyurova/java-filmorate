package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public List<String> onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<String> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            log.warn("MethodArgumentNotValidException: Validation failed for value [" +
                     fieldError.getRejectedValue() + "] in field " + fieldError.getObjectName() +
                     "." + fieldError.getField() + "; message: " + fieldError.getDefaultMessage());
        }
        return fieldErrors;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String onValidationException(ValidationException e) {
        return e.getMessage();
    }
}
