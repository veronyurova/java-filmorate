package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
            String message = "An id was passed (user id is assigned automatically)";
            log.warn("ValidationException at UserService.createUser: {}", message);
            throw new ValidationException(message);
        }
        user.setId(UserIdGenerator.getUserId());
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == 0) {
            String message = "An empty user id was passed";
            log.warn("ValidationException at UserService.updateUser: {}", message);
            throw new ValidationException(message);
        }
        User user = getUserById(newUser.getId());
        if (!user.getEmail().equals(newUser.getEmail())) {
            userStorage.checkEmailAvailability(newUser);
        }
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        user.setBirthday(newUser.getBirthday());
        return userStorage.updateUser(user);
    }

    public void addFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(int id, int friendId) {
        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getFriendsListById(int id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return getUserById(id).getFriends().stream()
                .filter(friendId -> getUserById(otherId).getFriends().contains(friendId))
                .map(this::getUserById).collect(Collectors.toList());
    }
}
