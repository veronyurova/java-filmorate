package ru.yandex.practicum.filmorate.model;

public class UserIdGenerator {
    private static int userId = 0;

    public static int getUserId() {
        userId++;
        return userId;
    }
}
