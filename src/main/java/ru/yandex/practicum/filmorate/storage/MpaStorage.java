package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

public interface MpaStorage {
    Pair getMpa(int mpaId);

    List<Pair> getAllMpa();

    Pair getMpaByFilmId(int filmId);
}
