package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public void addAsFriend(int userID, int friendID) {
        User user = userStorage.getUserByID(userID);
        User friend = userStorage.getUserByID(friendID);

        if (user.getFriends().stream().map(Friend::getFriendId).anyMatch(a -> a == friendID)) {
            throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s уже друзья", userID, friendID)
            );
        } else if (friend.getFriends().stream().map(Friend::getFriendId).anyMatch(a -> a == userID)) {
            friendsStorage.addAsFriend(userID, friendID, true);
            friendsStorage.confirmFriend(userID, friendID);
        } else {
            friendsStorage.addAsFriend(userID, friendID, false);
        }
    }

    public void removeFromFriends(int userID, int friendID) {
        User user = userStorage.getUserByID(userID);

        if (user.getFriends().contains(new Friend(friendID, false))) {
            friendsStorage.deleteFriend(userID, friendID);
        } else if (user.getFriends().contains(new Friend(friendID, true))) {
            friendsStorage.deleteFriend(userID, friendID);
            friendsStorage.deleteFriend(friendID, userID);
        } else {
            throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s не друзья", userID, friendID)
            );
        }
    }

    public List<User> getFriends(int userID) {
        log.info("возвращен перечень друзей пользователя с ID №{}", userID);
        return userStorage.getFriends(userID);
    }

    public List<User> getCommonFriends(int userID, int anotherUserID) {
        log.info("возвращен перечень общих друзей пользователей с ID №{} и №{}", userID, anotherUserID);
        return userStorage.getCommonFriends(userID, anotherUserID);
    }

    public List<User> getUsers() {
        log.info("Отправлен перечень пользователей");
        return userStorage.getUsers();
    }

    public User getUserByID(int userID) {
        log.info("Отправлен пользователь ID №{}", userID);
        return userStorage.getUserByID(userID);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }
}
