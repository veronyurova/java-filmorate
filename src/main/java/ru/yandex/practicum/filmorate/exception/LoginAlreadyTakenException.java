package ru.yandex.practicum.filmorate.exception;

public class LoginAlreadyTakenException extends RuntimeException {
    public LoginAlreadyTakenException(String message) {
        super(message);
    }
}
