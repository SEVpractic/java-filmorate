package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Pair;

import java.util.List;

public interface GenreStorage {
    List<Pair> getGenresByFilmID(int filmId);

    List<Pair> getAllGenres();

    Pair getGenre(int genreID);

    void removeGenres(int filmId);

    void createGenres(int filmId, List<Pair> genres);
}
