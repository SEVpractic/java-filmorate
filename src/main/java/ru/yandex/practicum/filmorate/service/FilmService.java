package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    public List<Film> getAll() {
        List<Film> films = filmStorage.getAll();
        if (!films.isEmpty()) films = genreStorage.fillFilmsByGenres(films);
        log.info("Отправлен перечень фильмов");
        return films;
    }

    public Film getByID(int filmID) {
        Film film = filmStorage.getByID(filmID);
        film = film.toBuilder().genres(genreStorage.getByFilmID(filmID)).build();
        log.info("Отправлен фильм ID № {}", filmID);
        return film;
    }

    public Film create(Film film) {
        film = filmStorage.create(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.create(film.getId(), film.getGenres());
        }
        log.info("Добавлен фильм id = {}", film.getId());
        return film;
    }

    public Film update(Film film) {
        film = filmStorage.update(film);
        genreStorage.remove(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreStorage.create(film.getId(), film.getGenres());
        }
        log.info("Обновлен фильм id = {}", film.getId());
        return film;
    }

    public void addLike(int filmID, int userID) {
        Like like = likeStorage.get(filmID, userID);
        if (like != null) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
        likeStorage.add(filmID, userID);
        log.info("Пользователь ID № {} поставил лайк фильму ID № {}", userID, filmID);
    }

    public void removeLike(int filmID, int userID) {
        Like like = likeStorage.get(filmID, userID);
        if (like == null) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
        likeStorage.remove(filmID, userID);
        log.info("Пользователь ID № {} удалил лайк фильму ID № {}", userID, filmID);
    }

    public List<Film> getPopular(int count) {
        List<Film> films = filmStorage.getPopular(count);
        if(!films.isEmpty()) films = genreStorage.fillFilmsByGenres(films);
        log.info("отправлено {} самых популярных фильмов", count);
        return films;
    }
}
