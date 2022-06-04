package ru.yandex.practicum.filmorate.storage;

public interface FilmGenreStorage {
    void addGenre(int id, int genreId);

    void deleteGenre(int id, int genreId);
}
