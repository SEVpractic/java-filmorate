package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmID, int userID) {
        filmStorage.isFilmExist(filmID);
        userStorage.isUserExist(userID);

        boolean isCorrect = filmStorage.getFilmByID(filmID).addLike(userID);

        if (!isCorrect) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
    }

    public void removeLike(int filmID, int userID) {
        filmStorage.isFilmExist(filmID);
        userStorage.isUserExist(userID);

        boolean isCorrect = filmStorage.getFilmByID(filmID).removeLike(userID);

        if (!isCorrect) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage
                .getFilms()
                .stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmByID(int filmID) {
        return filmStorage.getFilmByID(filmID);
    }


    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }
}
