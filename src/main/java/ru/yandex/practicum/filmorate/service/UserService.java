package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addAsFriend(int userID, int friendID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(friendID);

        boolean isCorrect = userStorage.getUserByID(userID).addFriend(friendID)
                && userStorage.getUserByID(friendID).addFriend(userID);

        if (!isCorrect) {
            throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s уже друзья", userID, friendID)
            );
        }
    }

    public void removeFromFriends(int userID, int friendID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(friendID);

        boolean isCorrect = userStorage.getUserByID(userID).removeFriend(friendID)
                && userStorage.getUserByID(friendID).removeFriend(userID);

        if (!isCorrect) {
            throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s не друзья", userID, friendID)
            );
        }
    }

    public List<User> getFriendsList(int userID) {
        userStorage.isUserExist(userID);

        return userStorage.getUserByID(userID)
                .getFriends()
                .stream()
                .map(userStorage::getUserByID)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriendsList(int userID, int anotherUserID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(anotherUserID);

        return userStorage.getUserByID(userID)
                .getFriends()
                .stream()
                .filter(id -> userStorage.getUserByID(anotherUserID).getFriends().contains(id))
                .filter(id -> id != anotherUserID)
                .map(userStorage::getUserByID)
                .collect(Collectors.toList());
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserByID(int userID) {
        return userStorage.getUserByID(userID);
    }


    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }
}
