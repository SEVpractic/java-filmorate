package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addAsFriend(int userID, int friendID, boolean isConfirmed);

    void confirmFriend(int userID, int friendID);

    void deleteFriend(int userID, int friendID);

    int getFriendsStatus(int userID, int friendID);

    List<User> getCommonFriends(int userID, int anotherUserID);

    List<User> getFriends(int userID);
}
