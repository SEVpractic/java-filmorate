package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.LikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

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
            throw new LikeException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
    }

    public void dellLike(int filmID, int userID) {
        filmStorage.isFilmExist(filmID);
        userStorage.isUserExist(userID);

        boolean isCorrect = filmStorage.getFilmByID(filmID).dellLike(userID);

        if (!isCorrect) {
            throw new LikeException(
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
}
