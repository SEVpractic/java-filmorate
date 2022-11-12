package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaControllerTest {
    private final MpaController mpaController;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaControllerTest(MpaController mpaController, MpaDbStorage mpaDbStorage) {
        this.mpaController = mpaController;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Test
    void mpa_controller_correct() {
        assertEquals(5, mpaController.getAllMpa().size(), "Не верно формируется список МПА");
        assertEquals(new Pair(3, "PG-13"), mpaController.getMpa(3), "Не верное выводится МПА");
        assertThrows(EntityNotExistException.class, () -> mpaController.getMpa(6),
                "Не выбрасывается исключение при запросе МПА несуществующего МПА");
        assertThrows(EntityNotExistException.class, () -> mpaDbStorage.getMpaByFilmId(100),
                "Не выбрасывается исключение при запросе МПА несуществующего фильма");
    }
}
