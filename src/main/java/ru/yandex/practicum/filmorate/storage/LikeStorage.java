package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorage {
    void add(int filmID, int userID);

    void remove(int filmID, int userID);

    Like get(int filmID, int userID);
}
