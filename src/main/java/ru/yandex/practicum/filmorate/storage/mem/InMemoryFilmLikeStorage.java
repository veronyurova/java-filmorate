package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

@Slf4j
@Component
public class InMemoryFilmLikeStorage implements FilmLikeStorage {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public InMemoryFilmLikeStorage(InMemoryFilmStorage inMemoryFilmStorage,
                                   InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void addLike(int id, int userId) {
        inMemoryUserStorage.getUserById(userId);
        Film film = inMemoryFilmStorage.getFilmById(id);
        film.getLikes().add(userId);
        log.info("InMemoryFilmLikeStorage.addLike: like for film {} " +
                 "from user {} successfully added", id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        inMemoryUserStorage.getUserById(userId);
        Film film = inMemoryFilmStorage.getFilmById(id);
        film.getLikes().remove(userId);
        log.info("InMemoryFilmLikeStorage.deleteLike: like for film {} " +
                 "from user {} successfully deleted", id, userId);
    }
}
