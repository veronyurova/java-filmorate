package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Component
@Primary
public class DatabaseFriendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseFriendshipStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sql = "INSERT INTO friendship (user_id, friend_id) " +
                     "VALUES (?, ?);";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sql = "DELETE FROM friendship " +
                     "WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, id, friendId);
    }
}
