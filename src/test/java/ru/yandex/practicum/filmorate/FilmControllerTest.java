package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.exceptions.OperationAlreadyCompletedException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest extends BdClassTests{
    private final FilmController filmController;
    private final UserController userController;

    @Test
    void film_controller_correct() {
        Film film = Film.builder().name("AAA").description("description").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).genres(null).build();
        Film film1 = Film.builder().name("BBB").description("description").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).genres(null).build();
        filmController.createFilm(film);
        filmController.createFilm(film1);
        film.setId(1);
        film1.setId(2);

        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film);
        expectedFilms.add(film1);
        film = film.toBuilder().genres(new ArrayList<>()).build();

        assertEquals(expectedFilms, filmController.getFilms(), "не корректно создается фильм");
        assertEquals(film, filmController.getFilmByID(1), "не корректно возвращается фильм по ID");

        Film film2 = Film.builder().id(1).name("AAA1").description("description1").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).build();
        filmController.updateFilm(film2);
        assertEquals("description1", filmController.getFilms().get(0).getDescription(),
                "обновление проходит не корректно");

        Film film3 = Film.builder().id(1000).name("AAA").description("description").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).build();
        assertThrows(EntityNotExistException.class, ()-> filmController.updateFilm(film3),
                "обновляется несуществующий фильм");

        assertEquals(1, filmController.getPopularFilms(1).size(),
                "не корректно формируется список count лучших фильмов");
        assertEquals(1, filmController.getPopularFilms(1).get(0).getId(),
                "не корректно формируется список count лучших фильмов");

        User user = User.builder().email("Rick@mail.com").login("Rick")
                .birthday(CORRECT_DATE).build();
        User user1 = User.builder().email("Mortie@mail.com").login("Mortie")
                .birthday(CORRECT_DATE).build();
        userController.createUser(user);
        userController.createUser(user1);

        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        assertThrows(OperationAlreadyCompletedException.class, () -> filmController.addLike(1, 1),
                "не выбрасывается исключение при постановке уже стоящего лайка");

        filmController.removeLike(1, 1);
        assertThrows(OperationAlreadyCompletedException.class, () -> filmController.removeLike(1, 1),
                "не выбрасывается исключение при удалении не стоявшего лайка");
    }
}