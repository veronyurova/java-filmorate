package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmIdGenerator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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
        log.info("FilmService.createFilm: film {} successfully created", film.getId());
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
        log.info("FilmService.updateFilm: film {} successfully updated", film.getId());
        return filmStorage.updateFilm(film);
    }

    public void addLike(int id, int userId) {
        userService.getUserById(userId);
        Film film = getFilmById(id);
        film.getLikes().add(userId);
        log.info("FilmService.addLike: like for film {} from user {} " +
                 "successfully added", id, userId);
    }

    public void deleteLike(int id, int userId) {
        userService.getUserById(userId);
        Film film = getFilmById(id);
        film.getLikes().remove(userId);
        log.info("FilmService.deleteLike: like for film {} from user {} " +
                 "successfully deleted", id, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return getFilmsList().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        return film2.getLikes().size() - film1.getLikes().size();
    }
}
