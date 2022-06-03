package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    List<User> getFriendsListById(int id);

    List<User> getCommonFriends(int id, int otherId);

    User getUserById(int id);

    User addUser(User user);

    User updateUser(User newUser);

    void deleteUserById(int id);
}
