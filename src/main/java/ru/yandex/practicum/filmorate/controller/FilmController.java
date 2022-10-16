package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    FilmStorage filmStorage;
    FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Отправлен перечень фильмов");
        return filmStorage.getFilms();
    }

    @GetMapping (path = "/{id}")
    public Film getFilmByID(@PathVariable ("id") int filmID) {
        log.info("Отправлен фильм ID № {}", filmID);
        return filmStorage.getFilmByID(filmID);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmStorage.createFilm(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmStorage.updateFilm(film);
        log.info("Обновлен фильм {}", film);
        return film;
    }

    @PutMapping(path = "/{id}/like/{userID}")
    public void addLike(@PathVariable ("id") int filmID,
                        @PathVariable int userID) {
        filmService.addLike(filmID, userID);
        log.info("Пользователь ID № {} поставил лайк фильму ID № {}", userID, filmID);
    }

    @DeleteMapping(path = "/{id}/like/{userID}")
    public void dellLike(@PathVariable ("id") int filmID,
                         @PathVariable int userID) {
        filmService.dellLike(filmID, userID);
        log.info("Пользователь ID № {} удалил лайк фильму ID № {}", userID, filmID);
    }

    @GetMapping(path = "/popular")
    public List<Film> getPopularFilms(@RequestParam (defaultValue = "10") int count) {
        log.info("отправлено {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
