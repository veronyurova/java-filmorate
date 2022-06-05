package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@Primary
public class DatabaseFIlmGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final static String ADD_GENRE_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) " +
            "VALUES (?, ?);";
    private final static String DELETE_GENRE_QUERY =
            "DELETE FROM film_genre " +
            "WHERE film_id = ? AND genre_id = ?;";
    private final static String GET_GENRES_BY_FILM_ID_QUERY =
            "SELECT genre_id FROM film_genre " +
            "WHERE film_id = ?;";

    @Autowired
    public DatabaseFIlmGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenre(int id, int genreId) {
        jdbcTemplate.update(ADD_GENRE_QUERY, id, genreId);
        log.info("DatabaseFilmGenreStorage.addGenre: genre {} for film {} " +
                 "successfully added", genreId, id);
    }

    @Override
    public void deleteGenre(int id, int genreId) {
        jdbcTemplate.update(DELETE_GENRE_QUERY, id, genreId);
        log.info("DatabaseFilmGenreStorage.deleteGenre: genre {} for film {} " +
                 "successfully deleted", genreId, id);
    }

    @Override
    public List<Integer> getGenresByFilmId(int id) {
        return jdbcTemplate.query(GET_GENRES_BY_FILM_ID_QUERY, this::mapRowToGenre, id);
    }

    private int mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("genre_id");
    }
}