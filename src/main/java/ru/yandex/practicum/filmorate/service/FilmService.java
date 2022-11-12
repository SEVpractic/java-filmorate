package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
    }

    public void addLike(int filmID, int userID) {
        Like like = likeStorage.getLike(filmID, userID);
        if (like != null) {
            throw new EntityNotExistException(
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
            throw new EntityNotExistException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
        likeStorage.addLike(filmID, userID);
        log.info("Пользователь ID № {} удалил лайк фильму ID № {}", userID, filmID);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("отправлено {} самых популярных фильмов", count);
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getFilms() {
        log.info("Отправлен перечень фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmByID(int filmID) {
        log.info("Отправлен фильм ID № {}", filmID);
        return filmStorage.getFilmByID(filmID);
    }


    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }
}
