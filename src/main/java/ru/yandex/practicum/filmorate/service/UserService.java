package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public List<User> getAll() {
        List<User> users = userStorage.get();
        log.info("Отправлен перечень пользователей");
        return users;
    }

    public User getByID(int userID) {
        User user = userStorage.getByID(userID);
        log.info("Отправлен пользователь ID №{}", userID);
        return user;
    }

    public User create(User user) {
        user = userStorage.create(user);
        log.info("Добавлен пользователь id = {}", user.getId());
        return user;
    }

    public User update(User user) {
        user = userStorage.update(user);
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    public void addAsFriend(int userID, int friendID) {
        switch (friendsStorage.getStatus(userID, friendID)) {
            // 0 - нет запроса, 1 - запрос отправлен, 2 - имеется только входящий запрос, 3 - запросы подтверждены.
            case 0:
                friendsStorage.add(userID, friendID, false);
                break;
            case 1:
                throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s уже " +
                            "отправил запрос пользователю с ID №%s", userID, friendID)
                );
            case 2:
                friendsStorage.add(userID, friendID, true);
                friendsStorage.confirm(userID, friendID);
                break;
            case 3:
                throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. " +
                            "Пользователи с ID № %s и %s уже друзья", userID, friendID)
                );
            default:
                throw new EntityNotExistException("Указанные пользователи не существуют");
        }
    }

    public void removeFromFriends(int userID, int friendID) {
        switch (friendsStorage.getStatus(userID, friendID)) {
            // 0 - нет запроса, 1 - запрос отправлен, 2 - имеется только входящий запрос, 3 - запросы подтверждены.
            case 0:
                throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s не друзья", userID, friendID)
                );
            case 1:
                friendsStorage.delete(userID, friendID);
                break;
            case 2:
                throw new OperationAlreadyCompletedException(
                    String.format("Невозможно выполнить. Пользователи с ID № %s и %s не друзья", userID, friendID)
                );
            case 3:
                friendsStorage.delete(userID, friendID);
                friendsStorage.delete(friendID, userID);
                break;
            default:
                throw new EntityNotExistException("Указанные пользователи не существуют");
        }
    }

    public List<User> getFriends(int userID) {
        log.info("возвращен перечень друзей пользователя с ID №{}", userID);
        return friendsStorage.getAll(userID);
    }

    public List<User> getCommonFriends(int userID, int anotherUserID) {
        log.info("возвращен перечень общих друзей пользователей с ID №{} и №{}", userID, anotherUserID);
        return friendsStorage.getCommon(userID, anotherUserID);
    }
}
