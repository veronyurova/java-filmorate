package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmIdGenerator;

import javax.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Map<Integer, Film> getAllFilms() {
        log.info("GET /film: request successfully processed");
        return films;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getId() != 0) {
            String message = "An id was passed (film id is assigned automatically)";
            log.warn("POST /film: ValidationException: " + message);
            throw new ValidationException(message);
        } else {
            film.setId(FilmIdGenerator.getFilmId());
            films.put(film.getId(), film);
            log.info("POST /film: film successfully created");
            return film;
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) throws ValidationException {
        if (newFilm.getId() == 0) {
            String message = "An empty film id was passed";
            log.warn("PUT /film: ValidationException: " + message);
            throw new ValidationException(message);
        } else if (!films.containsKey(newFilm.getId())) {
            String message = "There is no film with such id";
            log.warn("PUT /film: ValidationException: " + message);
            throw new ValidationException(message);
        } else {
            Film film = films.get(newFilm.getId());
            film.setName(newFilm.getName());
            film.setDescription(newFilm.getDescription());
            film.setReleaseDate(newFilm.getReleaseDate());
            film.setDuration(newFilm.getDuration());
            log.info("PUT /film: film successfully updated");
            return film;
        }
    }
}
