package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Отправлен перечень фильмов");
        return filmService.getFilms();
    }

    @GetMapping (path = "/{id}")
    public Film getFilmByID(@PathVariable ("id") int filmID) {
        log.info("Отправлен фильм ID № {}", filmID);
        return filmService.getFilmByID(filmID);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
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
    public void removeLike(@PathVariable ("id") int filmID,
                           @PathVariable int userID) {
        filmService.removeLike(filmID, userID);
        log.info("Пользователь ID № {} удалил лайк фильму ID № {}", userID, filmID);
    }

    @GetMapping(path = "/popular")
    public List<Film> getPopularFilms(@RequestParam (defaultValue = "10") int count) {
        log.info("отправлено {} самых популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
