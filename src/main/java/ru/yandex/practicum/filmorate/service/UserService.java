package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void createFriendship(int userID, int friendID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(friendID);

        boolean isCorrect = userStorage.getUserByID(userID).addFriend(friendID)
                && userStorage.getUserByID(friendID).addFriend(userID);

        if (!isCorrect) {
            throw new FriendshipException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s уже друзья", userID, friendID)
            );
        }
    }

    public void destroyFriendship(int userID, int friendID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(friendID);

        boolean isCorrect = userStorage.getUserByID(userID).dellFriend(friendID)
                && userStorage.getUserByID(friendID).dellFriend(userID);

        if (!isCorrect) {
            throw new FriendshipException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s не друзья", userID, friendID)
            );
        }
    }

    public List<User> getFriendsList(int userID) {
        userStorage.isUserExist(userID);

        return userStorage.getUserByID(userID)
                .getFriends()
                .stream()
                .map(id -> userStorage.getUserByID(id))
                .collect(Collectors.toList());
    }

    public List<User> getGenericFriendsList(int userID, int anotherUserID) {
        userStorage.isUserExist(userID);
        userStorage.isUserExist(anotherUserID);

        return userStorage.getUserByID(userID)
                .getFriends()
                .stream()
                .filter(id -> userStorage.getUserByID(anotherUserID).getFriends().contains(id))
                .filter(id -> id != anotherUserID)
                .map(id -> userStorage.getUserByID(id))
                .collect(Collectors.toList());
    }
}
