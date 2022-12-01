package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> get();

    User getByID(int userID);

    User create(User user);

    User update(User user);
}
