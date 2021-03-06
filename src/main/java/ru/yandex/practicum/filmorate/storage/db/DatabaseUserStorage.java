package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
    private final static String GET_USERS_QUERY =
            "SELECT id, " +
            "       email, " +
            "       login, " +
            "       name, " +
            "       birthday " +
            "FROM user;";
    private final static String GET_USER_BY_ID_QUERY =
            "SELECT id, " +
            "       email, " +
            "       login, " +
            "       name, " +
            "       birthday " +
            "FROM user " +
            "WHERE id = ?;";
    private final static String ADD_USER_QUERY =
            "INSERT INTO user (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?);";
    private final static String UPDATE_USER_QUERY =
            "UPDATE user " +
            "SET email = ?, " +
            "    login = ?, " +
            "    name = ?, " +
            "    birthday = ? " +
            "WHERE id = ?;";
    private final static String DELETE_USER_BY_ID_QUERY = "DELETE FROM user WHERE id = ?;";
    private final static String GET_FRIENDS_LIST_BY_ID_QUERY =
            "SELECT u.id, " +
            "       u.email, " +
            "       u.login, " +
            "       u.name, " +
            "       u.birthday " +
            "FROM user AS u " +
            "JOIN friendship AS sub ON u.id = sub.friend_id " +
            "WHERE sub.user_id = ?;";
    private final static String GET_COMMON_FRIENDS_QUERY =
            "SELECT u.id, " +
            "       u.email, " +
            "       u.login, " +
            "       u.name, " +
            "       u.birthday " +
            "FROM user AS u " +
            "JOIN friendship AS sub ON u.id = sub.friend_id " +
            "JOIN friendship AS sub_common ON sub.friend_id = sub_common.friend_id " +
            "WHERE sub.user_id = ? AND sub_common.user_id = ?;";

    @Autowired
    public DatabaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(GET_USERS_QUERY, this::mapRowToUser);
    }

    @Override
    public User getUserById(int id) {
        try {
            return jdbcTemplate.queryForObject(GET_USER_BY_ID_QUERY, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("There is no user with id %d", id);
            log.warn("UserNotFoundException at DatabaseUserStorage.getUserById: {}", message);
            throw new UserNotFoundException(message);
        }
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(ADD_USER_QUERY,
                                                                      new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        log.info("DatabaseUserStorage.addUser: user {} " +
                 "successfully added to database", user.getId());
        return getUserById(keyHolder.getKey().intValue());
    }

    @Override
    public User updateUser(User newUser) {
        jdbcTemplate.update(UPDATE_USER_QUERY, newUser.getEmail(), newUser.getLogin(),
                            newUser.getName(), newUser.getBirthday(), newUser.getId());
        log.info("DatabaseUserStorage.updateUser: user {} " +
                 "successfully updated", newUser.getId());
        return getUserById(newUser.getId());
    }

    @Override
    public void deleteUserById(int id) {
        jdbcTemplate.update(DELETE_USER_BY_ID_QUERY, id);
        log.info("DatabaseUserStorage.deleteUserById: user {} " +
                 "successfully deleted from database", id);
    }

    public List<User> getFriendsListById(int id) {
        return jdbcTemplate.query(GET_FRIENDS_LIST_BY_ID_QUERY, this::mapRowToUser, id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return jdbcTemplate.query(GET_COMMON_FRIENDS_QUERY, this::mapRowToUser, id, otherId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
