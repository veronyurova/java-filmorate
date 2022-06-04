package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

@Slf4j
@Repository
@Primary
public class DatabaseFIlmGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseFIlmGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenre(int id, int genreId) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, id, genreId);
        log.info("DatabaseFilmGenreStorage.addGenre: genre {} for film {} " +
                 "successfully added", genreId, id);
    }

    @Override
    public void deleteGenre(int id, int genreId) {
        String sql = "DELETE FROM film_genre " +
                     "WHERE film_id = ? AND genre_id = ?;";
        jdbcTemplate.update(sql, id, genreId);
        log.info("DatabaseFilmGenreStorage.deleteGenre: genre {} for film {} " +
                 "successfully deleted", genreId, id);
    }
}