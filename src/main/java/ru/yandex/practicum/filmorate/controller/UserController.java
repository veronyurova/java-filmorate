package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserIdGenerator;

import javax.validation.Valid;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Map<Integer, User> getAllUsers() {
        log.info("GET /user: request successfully processed");
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getId() != 0) {
            String message = "An id was passed (user id is assigned automatically)";
            log.warn("POST /user: ValidationException: " + message);
            throw new ValidationException(message);
        } else {
            for (Map.Entry<Integer, User> oldUser : users.entrySet()) {
                if (oldUser.getValue().getEmail().equals(user.getEmail())) {
                    String message = "A user with such an email already exists";
                    log.warn("POST /user: ValidationException: " + message);
                    throw new ValidationException(message);
                }
                if (oldUser.getValue().getLogin().equals(user.getLogin())) {
                    String message = "A user with such login already exists";
                    log.warn("POST /user: ValidationException: " + message);
                    throw new ValidationException(message);
                }
            }
            user.setId(UserIdGenerator.getUserId());
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("POST /user: user successfully created");
            return user;
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) throws ValidationException {
        if (newUser.getId() == 0) {
            String message = "An empty user id was passed";
            log.warn("PUT /user: ValidationException: " + message);
            throw new ValidationException(message);
        } else if (!users.containsKey(newUser.getId())) {
            String message = "There is no user with such id";
            log.warn("PUT /user: ValidationException: " + message);
            throw new ValidationException(message);
        } else {
            User user = users.get(newUser.getId());
            user.setName(newUser.getName());
            user.setBirthday(newUser.getBirthday());
            log.info("PUT /user: user successfully updated");
            return user;
        }
    }
}
