package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmIdGenerator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getFilmsList() {
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            String message = "An id was passed (film id is assigned automatically)";
            log.warn("ValidationException at FilmService.createFilm: {}", message);
            throw new ValidationException(message);
        }
        film.setId(FilmIdGenerator.getFilmId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == 0) {
            String message = "An empty film id was passed";
            log.warn("ValidationException at FilmService.updateFilm: {}", message);
            throw new ValidationException(message);
        }
        Film film = getFilmById(newFilm.getId());
        film.setName(newFilm.getName());
        film.setDescription(newFilm.getDescription());
        film.setReleaseDate(newFilm.getReleaseDate());
        film.setDuration(newFilm.getDuration());
        return filmStorage.updateFilm(film);
    }
}
