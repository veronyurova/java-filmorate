package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

@Slf4j
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public InMemoryFriendshipStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public void addFriend(int id, int friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        log.info("InMemoryFriendshipStorage.addFriend: friend {} " +
                 "successfully added to user {} friends", friendId, id);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        log.info("InMemoryFriendshipStorage.deleteFriend: friend {} " +
                 "successfully deleted from user {} friends", friendId, id);
    }
}
