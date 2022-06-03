package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Component
@Primary
public class DatabaseFIlmLikeStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseFIlmLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int id, int userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        String sql = "DELETE FROM film_like " +
                     "WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sql, id, userId);
    }
}
