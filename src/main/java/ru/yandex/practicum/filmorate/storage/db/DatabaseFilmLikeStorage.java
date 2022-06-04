package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

@Slf4j
@Repository
@Primary
public class DatabaseFilmLikeStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseFilmLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int id, int userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, id, userId);
        log.info("DatabaseFilmLikeStorage.addLike: like for film {} " +
                 "from user {} successfully added", id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sql = "DELETE FROM film_like " +
                     "WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, id, userId);
        log.info("DatabaseFilmLikeStorage.deleteLike: like for film {} " +
                 "from user {} successfully deleted", id, userId);
    }
}
