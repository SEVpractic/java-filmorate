package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    Set<Pair> getByFilmID(int filmId);

    List<Pair> getAll();

    Pair get(int genreID);

    List<Film> fillFilmsByGenres(List<Film> films);

    void remove(int filmId);

    void create(int filmId, Set<Pair> genres);
}
