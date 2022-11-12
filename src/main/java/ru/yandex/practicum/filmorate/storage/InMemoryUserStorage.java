package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage, FriendsStorage {
    private final Map<Integer, User> users;
    private int id;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
        this.id = 0;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserByID(int userID) {
        isUserExist(userID);
        return users.get(userID);
    }

    @Override
    public User createUser(User user) {
        createUserID(user);
        fillEmptyUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        isUserExist(user.getId());
        fillEmptyUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getCommonFriends(int userID, int anotherUserID) {
        return null;
    }

    private User createUserID(User user) {
        id++;
        user.setId(id);
        return user;
    }

    private boolean isUserExist(int userID) {
        boolean isExist = users.containsKey(userID);
        if (!isExist) {
            throw new EntityNotExistException(String.format("Пользователь c ID № %s не существует", userID));
        }
        return true;
    }

    private User fillEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

    @Override
    public List<User> getFriends(int userID) {
        return null;
    }

    @Override
    public void addAsFriend(int userID, int friendID, boolean isConfirmed) {

    }

    @Override
    public void confirmFriend(int userID, int friendID) {

    }

    @Override
    public void deleteFriend(int userID, int friendID) {

    }

    @Override
    public List<Friend> getFriendsList(int userID) {
        return null;
    }
}
