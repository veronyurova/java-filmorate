package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmIdGenerator;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

@RestController
@RequestMapping("/film")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Instant MIN_RELEASE_DATE = Instant.ofEpochMilli(-2335564800000L);

    @GetMapping
    public Map<Integer, Film> getAllFilms() {
        return films;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws ValidationException {
        if (film.getId() != 0) {
            throw new ValidationException("To create a film, you should not pass an id\n" +
                                          "The id is assigned automatically");
        } else if (film.getName() == null || film.getDescription() == null ||
                   film.getReleaseDate() == null || film.getName().isBlank() ||
                   film.getDescription().length() > 200 || film.getDuration() < 1 ||
                   film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Incorrect request body data");
        } else {
            film.setId(FilmIdGenerator.getFilmId());
            films.put(film.getId(), film);
            return film;
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) throws ValidationException {
        if (newFilm.getId() == 0) {
            throw new ValidationException("An empty film id was passed");
        } else if (!films.containsKey(newFilm.getId())) {
            throw new ValidationException("There is no film with such id");
        } else if (newFilm.getName() == null || newFilm.getDescription() == null ||
                   newFilm.getReleaseDate() == null || newFilm.getName().isBlank() ||
                   newFilm.getDescription().length() > 200 || newFilm.getDuration() < 1 ||
                   newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Incorrect request body data");
        } else {
            Film film = films.get(newFilm.getId());
            film.setName(newFilm.getName());
            film.setDescription(newFilm.getDescription());
            film.setReleaseDate(newFilm.getReleaseDate());
            film.setDuration(newFilm.getDuration());
            return film;
        }
    }
}
