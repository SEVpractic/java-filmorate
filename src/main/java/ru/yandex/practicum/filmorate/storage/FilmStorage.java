package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film getFilmByID(int filmID);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);
}
