package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
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
    public User createUserID(User user) {
        id++;
        user.setId(id);
        return user;
    }

    @Override
    public boolean isUserExist(int userID) {
        boolean isExist = users.containsKey(userID);
        if (!isExist) {
            throw new UserNotExistException(String.format("Пользователь c ID № %s не существует", userID));
        }
        return true;
    }

    @Override
    public User fillEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
