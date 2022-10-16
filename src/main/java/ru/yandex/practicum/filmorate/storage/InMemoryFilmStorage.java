package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotExistException;
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

    @Override
    public Film createFilmID(Film film) {
        id++;
        film.setId(id);
        return film;
    }

    @Override
    public boolean isFilmExist(int filmID) {
        boolean isExist = films.containsKey(filmID);
        if (!isExist) {
            throw new FilmNotExistException("Фильм c ID №" + filmID + " не существует");
        }
        return true;
    }
}
