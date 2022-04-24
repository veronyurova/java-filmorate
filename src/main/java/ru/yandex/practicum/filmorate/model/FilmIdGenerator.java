package ru.yandex.practicum.filmorate.model;

public class FilmIdGenerator {
    private static int filmId = 0;

    public static int getFilmId() {
        filmId++;
        return filmId;
    }
}
