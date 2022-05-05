package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            String message = "There is no film with such id";
            log.warn("FilmNotFoundException at InMemoryFilmStorage.getFilmById: {}", message);
            throw new FilmNotFoundException(message);
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        return newFilm;
    }

    @Override
    public void deleteFilmById(int id) {
        films.remove(id);
    }
}
