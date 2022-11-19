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
    public List<Film> get() {
        return filmService.getAll();
    }

    @GetMapping (path = "/{id}")
    public Film getByID(@PathVariable ("id") int filmID) {
        return filmService.getByID(filmID);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
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
    public List<Film> getPopular(@RequestParam (defaultValue = "10") @Positive int count) {
        return filmService.getPopular(count);
    }
}
