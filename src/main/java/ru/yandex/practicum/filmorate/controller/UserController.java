package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserIdGenerator;

import java.util.Map;
import java.util.HashMap;
import java.time.Instant;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        if (user.getId() != 0) {
            throw new ValidationException("To create a user, you should not pass an id\n" +
                                          "The id is assigned automatically");
        } else if (user.getEmail() == null || user.getLogin() == null ||
                   user.getName() == null || user.getBirthday() == null ||
                   user.getEmail().isBlank() || !user.getEmail().contains("@") ||
                   user.getLogin().isBlank() || user.getBirthday().isAfter(Instant.now())) {
            throw new ValidationException("Incorrect request body data");
        } else {
            for (Map.Entry<Integer, User> oldUser : users.entrySet())  {
                if (oldUser.getValue().getEmail().equals(user.getEmail())) {
                    throw new ValidationException("A user with such an email already exists");
                }
                if (oldUser.getValue().getLogin().equals(user.getLogin())) {
                    throw new ValidationException("A user with such login already exists");
                }
            }
            user.setId(UserIdGenerator.getUserId());
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return user;
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) throws ValidationException {
        if (newUser.getId() == 0) {
            throw new ValidationException("An empty user id was passed");
        } else if (!users.containsKey(newUser.getId())) {
            throw new ValidationException("There is no user with such id");
        } else if (newUser.getEmail() == null || newUser.getLogin() == null ||
                   newUser.getName() == null || newUser.getBirthday() == null ||
                   newUser.getEmail().isBlank() || !newUser.getEmail().contains("@") ||
                   newUser.getLogin().isBlank() || newUser.getBirthday().isAfter(Instant.now())) {
            throw new ValidationException("Incorrect request body data");
        } else {
            User user = users.get(newUser.getId());
            user.setName(newUser.getName());
            user.setBirthday(newUser.getBirthday());
            return user;
        }
    }
}
