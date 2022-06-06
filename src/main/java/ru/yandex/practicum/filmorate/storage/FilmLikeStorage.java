package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeStorage {
    void addLike(int id, int userId);

    void deleteLike(int id, int userId);
}
