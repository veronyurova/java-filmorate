package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserIdGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final static String EMPTY_ID_MESSAGE = "An empty user id was passed";
    private final static String NEEDLESS_ID_MESSAGE = "An id was passed " +
            "(user id is assigned automatically)";

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsersList() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public User createUser(User user) {
        if (user.getId() != 0) {
            log.warn("ValidationException at UserService.createUser: {}", NEEDLESS_ID_MESSAGE);
            throw new ValidationException(NEEDLESS_ID_MESSAGE);
        }
        user.setId(UserIdGenerator.getUserId());
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("UserService.createUser: user {} successfully created", user.getId());
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == 0) {
            log.warn("ValidationException at UserService.updateUser: {}", EMPTY_ID_MESSAGE);
            throw new ValidationException(EMPTY_ID_MESSAGE);
        }
        User user = getUserById(newUser.getId());
        if (!user.getEmail().equals(newUser.getEmail())) {
            userStorage.checkEmailAvailability(newUser);
        }
        if (!user.getLogin().equals(newUser.getLogin())) {
            userStorage.checkLoginAvailability(newUser);
        }
        user.setEmail(newUser.getEmail());
        user.setLogin(newUser.getLogin());
        user.setName(newUser.getName());
        user.setBirthday(newUser.getBirthday());
        log.info("UserService.updateUser: user {} successfully updated", user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("UserService.addFriend: friend {} successfully " +
                 "added to user {} friends", friendId, id);
    }

    public void deleteFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        log.info("UserService.deleteFriend: friend {} successfully " +
                 "deleted from user {} friends", friendId, id);
    }

    public List<User> getFriendsListById(int id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return getUserById(id).getFriends().stream()
                .filter(friendId -> getUserById(otherId).getFriends().contains(friendId))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }
}
