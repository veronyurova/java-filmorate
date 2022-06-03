package ru.yandex.practicum.filmorate.storage;

public interface FriendshipStorage {
    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);
}
