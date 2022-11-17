package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        if (!films.isEmpty()) films = genreStorage.fillFilmsByGenres(films);
        log.info("Отправлен перечень фильмов");
        return films;
    }

    public Film getFilmByID(int filmID) {
        Film film = filmStorage.getFilmByID(filmID);
        film = film.toBuilder().genres(genreStorage.getGenresByFilmID(filmID)).build();
        log.info("Отправлен фильм ID № {}", filmID);
        return film;
    }

    public Film createFilm(Film film) {
        film = setUniqGenres(film);
        film = filmStorage.createFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.createGenres(film.getId(), film.getGenres());
        }
        log.info("Добавлен фильм id = {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        film = setUniqGenres(film);
        film = filmStorage.updateFilm(film);
        genreStorage.removeGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.createGenres(film.getId(), film.getGenres());
        }
        log.info("Обновлен фильм id = {}", film.getId());
        return film;
    }

    public void addLike(int filmID, int userID) {
        Like like = likeStorage.getLike(filmID, userID);
        if (like != null) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
        likeStorage.addLike(filmID, userID);
        log.info("Пользователь ID № {} поставил лайк фильму ID № {}", userID, filmID);
    }

    public void removeLike(int filmID, int userID) {
        Like like = likeStorage.getLike(filmID, userID);
        if (like == null) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
        likeStorage.removeLike(filmID, userID);
        log.info("Пользователь ID № {} удалил лайк фильму ID № {}", userID, filmID);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        if(!films.isEmpty()) films = genreStorage.fillFilmsByGenres(films);
        log.info("отправлено {} самых популярных фильмов", count);
        return films;
    }

    private Film setUniqGenres(Film film) {
        List<Pair> genres = film.getGenres();
        if (genres == null) return film;
        Set<Pair> pairs = new HashSet<>(genres);
        genres = pairs.stream().sorted(Comparator.comparingInt(Pair::getId)).collect(Collectors.toList());
        return film.toBuilder().genres(genres).build();
    }
}
