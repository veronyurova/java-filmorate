package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

@Slf4j
@Repository
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
        log.info("DatabaseFriendshipStorage.addFriend: friend {} " +
                 "successfully added to user {} friends", friendId, id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        String sql = "DELETE FROM friendship " +
                     "WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sql, id, friendId);
        log.info("DatabaseFriendshipStorage.deleteFriend: friend {} " +
                 "successfully deleted from user {} friends", friendId, id);
    }
}
