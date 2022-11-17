package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

public interface GenreStorage {
    List<Pair> getGenresByFilmID(int filmId);

    List<Pair> getAllGenres();

    Pair getGenre(int genreID);

    List<Film> fillFilmsByGenres(List<Film> films);

    void removeGenres(int filmId);

    void createGenres(int filmId, List<Pair> genres);
}
