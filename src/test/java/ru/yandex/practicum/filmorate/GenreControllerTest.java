package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreControllerTest {
    private final GenreController genreController;

    @Autowired
    public GenreControllerTest(GenreController genreController) {
        this.genreController = genreController;
    }

    @Test
    void map_controller_correct() {
        assertEquals(6, genreController.getAllGenres().size(),
                "Не корректно формируется списко жанров");
        assertEquals(new Pair(3, "Мультфильм"), genreController.getGenre(3),
                "Не корректно возвращается жанр по ID");
        assertThrows(EntityNotExistException.class, () -> genreController.getGenre(7),
                "не выбрасывается исключение при попытке запросить несуществующий жанр");
    }
}
