package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void add(int userID, int friendID, boolean isConfirmed);

    void confirm(int userID, int friendID);

    void delete(int userID, int friendID);

    int getStatus(int userID, int friendID);

    List<User> getCommon(int userID, int anotherUserID);

    List<User> getAll(int userID);
}
