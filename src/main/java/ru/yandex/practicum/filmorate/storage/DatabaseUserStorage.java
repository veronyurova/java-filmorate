package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@Primary
public class DatabaseUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT id, " +
                     "       email, " +
                     "       login, " +
                     "       name, " +
                     "       birthday " +
                     "FROM user;";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public List<User> getFriendsListById(int id) {
        String sql = "SELECT u.id, " +
                     "       u.email, " +
                     "       u.login, " +
                     "       u.name, " +
                     "       u.birthday " +
                     "FROM user AS u " +
                     "JOIN friendship AS sub ON u.id = sub.friend_id " +
                     "WHERE sub.user_id = ?;";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "SELECT u.id, " +
                     "       u.email, " +
                     "       u.login, " +
                     "       u.name, " +
                     "       u.birthday " +
                     "FROM user AS u " +
                     "JOIN friendship AS sub ON u.id = sub.friend_id " +
                     "JOIN friendship AS sub_common ON sub.friend_id = sub_common.friend_id " +
                     "WHERE sub.user_id = ? AND sub_common.user_id = ?;";
        return jdbcTemplate.query(sql, this::mapRowToUser, id, otherId);
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT id, " +
                     "       email, " +
                     "       login, " +
                     "       name, " +
                     "       birthday " +
                     "FROM user " +
                     "WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no user with id %d", id);
            log.warn("UserNotFoundException at DatabaseUserStorage.getUserById: {}", message);
            throw new UserNotFoundException(message);
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO user (email, login, name, birthday) " +
                     "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        return getUserById(keyHolder.getKey().intValue());
    }

    @Override
    public User updateUser(User newUser) {
        String sql = "UPDATE user " +
                     "SET email = ?, " +
                     "    login = ?, " +
                     "    name = ?, " +
                     "    birthday = ? " +
                     "WHERE id = ?;";
        jdbcTemplate.update(sql, newUser.getEmail(), newUser.getLogin(),
                            newUser.getName(), newUser.getBirthday(), newUser.getId());
        return getUserById(newUser.getId());
    }

    @Override
    public void deleteUserById(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
