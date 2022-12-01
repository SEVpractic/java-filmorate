package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();

    Film getByID(int filmID);

    Film create(Film film);

    Film update(Film film);

    List<Film> getPopular(int count);
}
