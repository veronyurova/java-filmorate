package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final UserService userService;
    private final static String EMPTY_ID_MESSAGE = "An empty film id was passed";
    private final static String NEEDLESS_ID_MESSAGE = "An id was passed " +
                                                      "(film id is assigned automatically)";

    @Autowired
    public FilmService(FilmStorage filmStorage, FilmLikeStorage filmLikeStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.filmLikeStorage = filmLikeStorage;
        this.userService = userService;
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilms();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        if (film.getId() != 0) {
            log.warn("ValidationException at FilmService.createFilm: {}", NEEDLESS_ID_MESSAGE);
            throw new ValidationException(NEEDLESS_ID_MESSAGE);
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == 0) {
            log.warn("ValidationException at FilmService.updateFilm: {}", EMPTY_ID_MESSAGE);
            throw new ValidationException(EMPTY_ID_MESSAGE);
        }
        getFilmById(newFilm.getId());
        return filmStorage.updateFilm(newFilm);
    }

    public void addLike(int id, int userId) {
        getFilmById(id);
        userService.getUserById(userId);
        filmLikeStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        getFilmById(id);
        userService.getUserById(userId);
        filmLikeStorage.deleteLike(id, userId);
    }
}
