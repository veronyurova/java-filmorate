package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final static String EMPTY_ID_MESSAGE = "An empty user id was passed";
    private final static String NEEDLESS_ID_MESSAGE = "An id was passed " +
            "(user id is assigned automatically)";

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public List<User> getUsersList() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            log.warn("ValidationException at UserService.createUser: {}", NEEDLESS_ID_MESSAGE);
            throw new ValidationException(NEEDLESS_ID_MESSAGE);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == 0) {
            log.warn("ValidationException at UserService.updateUser: {}", EMPTY_ID_MESSAGE);
            throw new ValidationException(EMPTY_ID_MESSAGE);
        }
        getUserById(newUser.getId());
        return userStorage.updateUser(newUser);
    }

    public void addFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipStorage.addFriend(id, friendId);
        log.info("UserService.addFriend: friend {} successfully " +
                 "added to user {} friends", friendId, id);
    }

    public void deleteFriend(int id, int friendId) {
        getUserById(id);
        getUserById(friendId);
        friendshipStorage.deleteFriend(id, friendId);
        log.info("UserService.deleteFriend: friend {} successfully " +
                 "deleted from user {} friends", friendId, id);
    }

    public List<User> getFriendsListById(int id) {
        getUserById(id);
        return userStorage.getFriendsListById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        getUserById(id);
        getUserById(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }
}
