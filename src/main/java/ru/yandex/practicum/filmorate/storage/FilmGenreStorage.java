package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface FilmGenreStorage {
    void addGenre(int id, int genreId);

    void deleteGenre(int id, int genreId);

    List<Integer> getGenresByFilmId(int id);
}
