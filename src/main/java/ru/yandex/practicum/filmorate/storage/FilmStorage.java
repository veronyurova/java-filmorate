package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    List<Film> getPopularFilms(int count);

    Film getFilmById(int id);

    Film addFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteFilmById(int id);
}
