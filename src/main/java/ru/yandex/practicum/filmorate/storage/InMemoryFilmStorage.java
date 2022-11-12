package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int id;

    @Autowired
    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.id = 0;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmByID(int filmID) {
        isFilmExist(filmID);
        return films.get(filmID);
    }

    @Override
    public Film createFilm(Film film) {
        createFilmID(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        isFilmExist(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    private Film createFilmID(Film film) {
        id++;
        film.setId(id);
        return film;
    }

    private boolean isFilmExist(int filmID) {
        boolean isExist = films.containsKey(filmID);
        if (!isExist) {
            throw new EntityNotExistException("Фильм c ID №" + filmID + " не существует");
        }
        return true;
    }

    public void addLike(int filmID, int userID) {
        //filmStorage.isFilmExist(filmID);
        //userStorage.isUserExist(userID);

        //boolean isCorrect = filmStorage.getFilmByID(filmID).addLike(userID);
        boolean isCorrect = true;

        if (true) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно поставить лайк, т.к. пользователь ID №%s уже поставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
    }

    public void removeLike(int filmID, int userID) {
        //filmStorage.isFilmExist(filmID);
        //userStorage.isUserExist(userID);

        //boolean isCorrect = filmStorage.getFilmByID(filmID).removeLike(userID);
        boolean isCorrect = true;

        if (!isCorrect) {
            throw new OperationAlreadyCompletedException(
                    String.format(
                            "Невозможно удалить лайк, т.к. пользователь ID №%s не ставил лайк фильму ID №%s",
                            userID, filmID)
            );
        }
    }

    public List<Film> getPopularFilms(int count) {
        /*return filmStorage
                .getFilms()
                .stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());*/
        return null;
    }
}
