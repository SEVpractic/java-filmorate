package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorage {
    void addLike(int filmID, int userID);

    void removeLike(int filmID, int userID);

    Like getLike(int filmID, int userID);
}
