package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@Primary
public class DatabaseFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT f.id, " +
                     "       f.name, " +
                     "       f.description, " +
                     "       f.release_date, " +
                     "       f.duration, " +
                     "       mpa.id AS mpa_id, " +
                     "       mpa.name AS mpa_name " +
                     "FROM film AS f " +
                     "JOIN mpa ON f.mpa_id = mpa.id;";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, " +
                     "       f.name, " +
                     "       f.description, " +
                     "       f.release_date, " +
                     "       f.duration, " +
                     "       mpa.id AS mpa_id, " +
                     "       mpa.name AS mpa_name " +
                     "FROM film AS f " +
                     "JOIN mpa ON f.mpa_id = mpa.id " +
                     "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
                     "GROUP BY f.id " +
                     "ORDER BY COUNT(fl.user_id) DESC " +
                     "LIMIT ?;";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    @Override
    public Film getFilmById(int id) {
        String sql = "SELECT f.id, " +
                     "       f.name, " +
                     "       f.description, " +
                     "       f.release_date, " +
                     "       f.duration, " +
                     "       mpa.id AS mpa_id, " +
                     "       mpa.name AS mpa_name " +
                     "FROM film AS f " +
                     "JOIN mpa ON f.mpa_id = mpa.id " +
                     "WHERE f.id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no film with id %d", id);
            log.warn("FilmNotFoundException at DatabaseFilmStorage.getFIlmById: {}", message);
            throw new FilmNotFoundException(message);
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, mpa_id) " +
                     "VALUES (?, ?, ?, ?, ?);";
        checkMpa(film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        log.info("DatabaseFilmStorage.addFilm: film {} " +
                 "successfully added to database", film.getId());
        return getFilmById(keyHolder.getKey().intValue());
    }

    @Override
    public Film updateFilm(Film newFilm) {
        String sql = "UPDATE film " +
                     "SET name = ?, " +
                     "    description = ?, " +
                     "    release_date = ?, " +
                     "    duration = ?, " +
                     "    mpa_id = ? " +
                     "WHERE id = ?;";
        checkMpa(newFilm.getMpa().getId());
        jdbcTemplate.update(sql, newFilm.getName(), newFilm.getDescription(),
                            newFilm.getReleaseDate(), newFilm.getDuration(),
                            newFilm.getMpa().getId(), newFilm.getId());
        log.info("DatabaseFilmStorage.updateFilm: film {} " +
                 "successfully updated", newFilm.getId());
        return getFilmById(newFilm.getId());
    }

    @Override
    public void deleteFilmById(int id) {
        String sql = "DELETE FROM film WHERE id = ?;";
        jdbcTemplate.update(sql, id);
        log.info("DatabaseFilmStorage.deleteFilmById: film {} " +
                 "successfully deleted from database", id);
    }

    public List<Film> getFilmsByGenre(int id) {
        String sql = "SELECT f.id, " +
                     "       f.name, " +
                     "       f.description, " +
                     "       f.release_date, " +
                     "       f.duration, " +
                     "       mpa.id AS mpa_id, " +
                     "       mpa.name AS mpa_name " +
                     "FROM film AS f " +
                     "JOIN mpa ON f.mpa_id = mpa.id " +
                     "JOIN film_genre AS fg ON f.id = fg.film_id " +
                     "WHERE fg.genre_id = ?;";
        checkGenre(id);
        return jdbcTemplate.query(sql, this::mapRowToFilm, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        int mpaId = resultSet.getInt("mpa_id");
        String mpaName = resultSet.getString("mpa_name");
        Mpa mpa = new Mpa(mpaId, mpaName);
        return new Film(id, name, description, releaseDate, duration, mpa);
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new Mpa(id, name);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    private void checkMpa(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?;";
        try {
            jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no MPA with id %d", id);
            log.warn("MpaNotFoundException at DatabaseFilmStorage.checkMpa: {}", message);
            throw new MpaNotFoundException(message);
        }
    }

    private void checkGenre(int id) {
        String sql = "SELECT * FROM genre WHERE id = ?;";
        try {
            jdbcTemplate.query(sql, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no genre with id %d", id);
            log.warn("GenreNotFoundException at DatabaseFilmStorage.checkGenre: {}", message);
            throw new GenreNotFoundException(message);
        }
    }
}
