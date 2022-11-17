package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping (path = "/{id}")
    public Film getFilmByID(@PathVariable ("id") int filmID) {
        return filmService.getFilmByID(filmID);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(path = "/{id}/like/{userID}")
    public void addLike(@PathVariable ("id") int filmID,
                        @PathVariable int userID) {
        filmService.addLike(filmID, userID);
    }

    @DeleteMapping(path = "/{id}/like/{userID}")
    public void removeLike(@PathVariable ("id") int filmID,
                           @PathVariable int userID) {
        filmService.removeLike(filmID, userID);
    }

    @GetMapping(path = "/popular")
    public List<Film> getPopularFilms(@RequestParam (defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilms(count);
    }
}
