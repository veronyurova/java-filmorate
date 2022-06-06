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
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Repository
@Primary
public class DatabaseFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final static String GET_FILMS_QUERY =
            "SELECT f.id, " +
            "       f.name, " +
            "       f.description, " +
            "       f.release_date, " +
            "       f.duration, " +
            "       mpa.id AS mpa_id, " +
            "       mpa.name AS mpa_name, " +
            "       GROUP_CONCAT(fl.user_id) AS user_ids, " +
            "       GROUP_CONCAT(fg.genre_id) AS genre_ids " +
            "FROM film AS f " +
            "JOIN mpa ON f.mpa_id = mpa.id " +
            "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
            "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
            "GROUP BY f.id;";
    private final static String GET_POPULAR_FILMS_QUERY =
            "SELECT f.id, " +
            "       f.name, " +
            "       f.description, " +
            "       f.release_date, " +
            "       f.duration, " +
            "       mpa.id AS mpa_id, " +
            "       mpa.name AS mpa_name, " +
            "       GROUP_CONCAT(fl.user_id) AS user_ids, " +
            "       GROUP_CONCAT(fg.genre_id) AS genre_ids " +
            "FROM film AS f " +
            "JOIN mpa ON f.mpa_id = mpa.id " +
            "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
            "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?;";
    private final static String GET_FILM_BY_ID_QUERY =
            "SELECT f.id, " +
            "       f.name, " +
            "       f.description, " +
            "       f.release_date, " +
            "       f.duration, " +
            "       mpa.id AS mpa_id, " +
            "       mpa.name AS mpa_name, " +
            "       GROUP_CONCAT(fl.user_id) AS user_ids, " +
            "       GROUP_CONCAT(fg.genre_id) AS genre_ids " +
            "FROM film AS f " +
            "JOIN mpa ON f.mpa_id = mpa.id " +
            "LEFT JOIN film_like AS fl ON f.id = fl.film_id " +
            "LEFT JOIN film_genre AS fg ON f.id = fg.film_id " +
            "WHERE f.id = ? " +
            "GROUP BY f.id;";
    private final static String ADD_FILM_QUERY =
            "INSERT INTO film (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?);";
    private final static String UPDATE_FILM_QUERY =
            "UPDATE film " +
            "SET name = ?, " +
            "    description = ?, " +
            "    release_date = ?, " +
            "    duration = ?, " +
            "    mpa_id = ? " +
            "WHERE id = ?;";
    private final static String DELETE_FILM_BY_ID_QUERY = "DELETE FROM film WHERE id = ?;";
    private final static String GET_FILMS_BY_GENRE_QUERY =
            "SELECT f.id, " +
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

    @Autowired
    public DatabaseFilmStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage,
                               FilmLikeStorage filmLikeStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
        this.filmLikeStorage = filmLikeStorage;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query(GET_FILMS_QUERY, this::mapRowToFilm);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(GET_POPULAR_FILMS_QUERY, this::mapRowToFilm, count);
    }

    @Override
    public Film getFilmById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_FILM_BY_ID_QUERY, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no film with id %d", id);
            log.warn("FilmNotFoundException at DatabaseFilmStorage.getFIlmById: {}", message);
            throw new FilmNotFoundException(message);
        }
    }

    @Override
    public Film addFilm(Film film) {
        checkMpa(film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(ADD_FILM_QUERY,
                                                                      new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        for (int userId : film.getLikes()) {
            filmLikeStorage.addLike(id, userId);
        }
        for (int genreId : film.getGenres()) {
            filmGenreStorage.addGenre(id, genreId);
        }
        log.info("DatabaseFilmStorage.addFilm: film {} successfully added to database", id);
        return getFilmById(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        checkMpa(newFilm.getMpa().getId());
        int id = newFilm.getId();
        List<Integer> oldFilmGenres = filmGenreStorage.getGenresByFilmId(id);
        for (int genreId : oldFilmGenres) {
            filmGenreStorage.deleteGenre(id, genreId);
        }
        jdbcTemplate.update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(),
                            newFilm.getReleaseDate(), newFilm.getDuration(),
                            newFilm.getMpa().getId(), newFilm.getId());
        for (int genreId : newFilm.getGenres()) {
            filmGenreStorage.addGenre(id, genreId);
        }
        log.info("DatabaseFilmStorage.updateFilm: film {} successfully updated", id);
        return getFilmById(id);
    }

    @Override
    public void deleteFilmById(int id) {
        jdbcTemplate.update(DELETE_FILM_BY_ID_QUERY, id);
        log.info("DatabaseFilmStorage.deleteFilmById: film {} " +
                 "successfully deleted from database", id);
    }

    public List<Film> getFilmsByGenre(int id) {
        checkGenre(id);
        return jdbcTemplate.query(GET_FILMS_BY_GENRE_QUERY, this::mapRowToFilm, id);
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
        HashSet<Integer> likes = new HashSet<>();
        String likesRaw = resultSet.getString("user_ids");
        if (likesRaw != null) {
            String[] userIdsRaw = likesRaw.split(",");
            for (String userIdRaw : userIdsRaw) {
                int userId = Integer.parseInt(userIdRaw);
                likes.add(userId);
            }
        }
        HashSet<Integer> genres = new HashSet<>();
        String genresRaw = resultSet.getString("genre_ids");
        if (genresRaw != null) {
            String[] genresIdsRaw = genresRaw.split(",");
            for (String genreIdRaw : genresIdsRaw) {
                int genreId = Integer.parseInt(genreIdRaw);
                genres.add(genreId);
            }
        }
        return new Film(id, name, description, releaseDate, duration, mpa, likes, genres);
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
