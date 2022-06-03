package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserIdGenerator;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyTakenException;
import ru.yandex.practicum.filmorate.exception.LoginAlreadyTakenException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getFriendsListById(int id) {
        return getUserById(id).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        return getUserById(id).getFriends().stream()
                .filter(friendId -> getUserById(otherId).getFriends().contains(friendId))
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            String message = String.format("There is no user with id %d", id);
            log.warn("UserNotFoundException at InMemoryUserStorage.getUserById: {}", message);
            throw new UserNotFoundException(message);
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        checkEmailAvailability(user);
        checkLoginAvailability(user);
        user.setId(UserIdGenerator.getUserId());
        users.put(user.getId(), user);
        log.info("InMemoryUserStorage.addUser: user {} " +
                 "successfully added to storage", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        User user = getUserById(newUser.getId());
        if (!user.getEmail().equals(newUser.getEmail())) {
            checkEmailAvailability(newUser);
        }
        if (!user.getLogin().equals(newUser.getLogin())) {
            checkLoginAvailability(newUser);
        }
        user.setEmail(newUser.getEmail());
        user.setLogin(newUser.getLogin());
        user.setName(newUser.getName());
        user.setBirthday(newUser.getBirthday());
        log.info("InMemoryUserStorage.updateUser: user {} successfully updated", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
        log.info("InMemoryUserStorage.deleteUserById: user {} " +
                 "successfully deleted from storage", id);
    }

    private void checkEmailAvailability(User newUser) {
        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                String message = String.format("Email %s is already taken", newUser.getEmail());
                log.warn("EmailAlreadyTakenException at InMemoryUserStorage.addUser: {}", message);
                throw new EmailAlreadyTakenException(message);
            }
        }
    }

    private void checkLoginAvailability(User newUser) {
        for (User user : users.values()) {
            if (user.getLogin().equals(newUser.getLogin())) {
                String message = String.format("Login %s is already taken", newUser.getLogin());
                log.warn("LoginAlreadyTakenException at InMemoryUserStorage.addUser: {}", message);
                throw new LoginAlreadyTakenException(message);
            }
        }
    }
}
