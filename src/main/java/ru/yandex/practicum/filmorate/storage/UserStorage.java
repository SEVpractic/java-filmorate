package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User getUserByID(int userID);

    User createUser(User user);

    User updateUser(User user);

    User createUserID(User user);

    boolean isUserExist(int userID);

    User fillEmptyUserName(User user);
}
