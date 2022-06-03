package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmIdGenerator;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return getFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            String message = String.format("There is no film with id %d", id);
            log.warn("FilmNotFoundException at InMemoryFilmStorage.getFilmById: {}", message);
            throw new FilmNotFoundException(message);
        }
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(FilmIdGenerator.getFilmId());
        films.put(film.getId(), film);
        log.info("InMemoryFilmStorage.addFilm: film {} " +
                 "successfully added to storage", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        Film film = getFilmById(newFilm.getId());
        film.setName(newFilm.getName());
        film.setDescription(newFilm.getDescription());
        film.setReleaseDate(newFilm.getReleaseDate());
        film.setDuration(newFilm.getDuration());
        log.info("InMemoryFilmStorage.updateFilm: film {} successfully updated", film.getId());
        return film;
    }

    @Override
    public void deleteFilmById(int id) {
        films.remove(id);
        log.info("InMemoryFilmStorage.deleteFilmById: film {} " +
                 "successfully deleted from storage", id);
    }

    private int compare(Film film1, Film film2) {
        return film2.getLikes().size() - film1.getLikes().size();
    }
}
