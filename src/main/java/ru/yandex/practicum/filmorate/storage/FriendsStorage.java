package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;

public interface FriendsStorage {
    void addAsFriend(int userID, int friendID, boolean isConfirmed);

    void confirmFriend(int userID, int friendID);

    void deleteFriend(int userID, int friendID);

    List<Friend> getFriendsList(int userID);
}
