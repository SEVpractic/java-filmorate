package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

public interface MpaStorage {
    Pair get(int mpaId);

    List<Pair> getAll();

    Pair getByFilmId(int filmId);
}
