package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.EmailAlreadyTakenException;
import ru.yandex.practicum.filmorate.exception.LoginAlreadyTakenException;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Map<Integer, User> getUsers() {
        return users;
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
        users.put(user.getId(), user);
        log.info("InMemoryUserStorage.addUser: user {} " +
                 "successfully added to storage", user.getId());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        return newUser;
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
        log.info("InMemoryUserStorage.deleteUserById: user {} " +
                 "successfully deleted from storage", id);
    }

    @Override
    public void checkEmailAvailability(User newUser) {
        for (User user : users.values()) {
            if (user.getEmail().equals(newUser.getEmail())) {
                String message = String.format("Email %s is already taken", newUser.getEmail());
                log.warn("EmailAlreadyTakenException at InMemoryUserStorage.addUser: {}", message);
                throw new EmailAlreadyTakenException(message);
            }
        }
    }

    @Override
    public void checkLoginAvailability(User newUser) {
        for (User user : users.values()) {
            if (user.getLogin().equals(newUser.getLogin())) {
                String message = String.format("Login %s is already taken", newUser.getLogin());
                log.warn("LoginAlreadyTakenException at InMemoryUserStorage.addUser: {}", message);
                throw new LoginAlreadyTakenException(message);
            }
        }
    }
}
