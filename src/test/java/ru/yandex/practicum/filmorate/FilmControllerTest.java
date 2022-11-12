package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {
    private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 10, 3);
    private static final LocalDate UN_CORRECT_DATE_RELISE = LocalDate.of(1000, 12, 3);
    private static final Duration CORRECT_DURATION = Duration.ofMinutes(120);

    private static Validator validator;
    private final FilmController filmController;

    @Autowired
    public FilmControllerTest(FilmController filmController) {
        this.filmController = filmController;
    }

    @BeforeAll
    static void buildValidator() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void film_validation_correct() {
        Film film = new Film(null, "AAA BBB CCC", "description", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, null));
        assertEquals(0, validator.validate(film).size(), "отсеивается корректный фильм");

        Film film1 = new Film(null, null, "description", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, null));
        assertNotEquals(0, validator.validate(film1).size(),
                "не корректная валидация по пустому имени");

        Film film2 = new Film(null, "AAA BBB CCC",
                "KgIGmWllKP2ysubdsPcJelTnLe08qxRZ7fYQ6B5ISLOgJxnxw9qA4B7FMexiTDoqGGJenXN9D8KaGwgGg0onl" +
                        "VrADNnHi9PUAV4XPJsafP09pTYy4HUTYoe3Ju2SDIYvZfGemqskAWuASKlNoKUTYva31VzYp7ukuvSJf8x7PsQ" +
                        "ddfh7mzxcUmBPY7tZtrcD4IGh6Qe4GqyT0qBMAxPJf6voqGOweOkOMCSE406JsZ3FIRMsPa87Uhp",
                CORRECT_DATE, CORRECT_DURATION, 10, new ArrayList<Pair>(), new Pair(1, null));
        assertNotEquals(0, validator.validate(film2).size(),
                "не корректная валидация при количестве символов более 200 в описании");

        Film film3 = new Film(null, "AAA BBB CCC", "description", CORRECT_DATE, Duration.ZERO, 10,
                new ArrayList<Pair>(), new Pair(1, null));
        assertNotEquals(0, validator.validate(film3).size(),
                "Некорректная валидация при длительности ноль");

        Film film4 = new Film(null, "AAA BBB CCC", "description",
                UN_CORRECT_DATE_RELISE, CORRECT_DURATION, 10, new ArrayList<Pair>(), new Pair(1, null));
        assertNotEquals(0, validator.validate(film4).size(),
                "Некорректная валидация неверной даты релиза");
    }

    @Test
    void film_controller_correct() {
        Film film = new Film(null, "AAA", "description", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, "G"));
        Film film1 = new Film(null, "BBB", "description", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, "G"));
        filmController.createFilm(film);
        filmController.createFilm(film1);
        film.setId(1);
        film1.setId(2);

        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film);
        expectedFilms.add(film1);

        assertEquals(expectedFilms, filmController.getFilms(), "не корректно создается фильм");
        assertEquals(film, filmController.getFilmByID(1), "не корректно возвращается фильм по ID");

        Film film2 = new Film(1, "AAA", "description1", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, null));
        filmController.updateFilm(film2);

        assertEquals("description1", filmController.getFilms().get(0).getDescription(),
                "обновление проходит не корректно");

        Film film3 = new Film(1000, "AAA", "description1", CORRECT_DATE, CORRECT_DURATION, 10,
                new ArrayList<Pair>(), new Pair(1, null));
        assertThrows(EntityNotExistException.class, ()-> filmController.updateFilm(film3),
                "обновляется несуществующий фильм");

        assertEquals(1, filmController.getPopularFilms(1).size(),
                "не корректно формируется список count лучших фильмов");
        assertEquals(1, filmController.getPopularFilms(1).get(0).getId(),
                "не корректно формируется список count лучших фильмов");

        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        assertThrows(OperationAlreadyCompletedException.class, () -> filmController.addLike(1, 1),
                "не выбрасывается исключение при постановке уже стоящего лайка");

        filmController.removeLike(1, 1);
        assertThrows(OperationAlreadyCompletedException.class, () -> filmController.removeLike(1, 1),
                "не выбрасывается исключение при удалении не стоявшего лайка");
    }
}