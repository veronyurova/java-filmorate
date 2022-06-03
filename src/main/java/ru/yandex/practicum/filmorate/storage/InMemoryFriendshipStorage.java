package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;

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
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        User user = inMemoryUserStorage.getUserById(id);
        inMemoryUserStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
    }
}
