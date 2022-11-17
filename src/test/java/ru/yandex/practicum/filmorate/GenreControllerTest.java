package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest extends BdClassTests{
    private final GenreController genreController;

    @Test
    void map_controller_correct() {
        assertEquals(6, genreController.getAllGenres().size(),
                "Не корректно формируется списко жанров");
        assertEquals(3, genreController.getGenre(3).getId(),
                "Не корректно возвращается жанр по ID");
        assertThrows(EntityNotExistException.class, () -> genreController.getGenre(7),
                "не выбрасывается исключение при попытке запросить несуществующий жанр");
    }
}
