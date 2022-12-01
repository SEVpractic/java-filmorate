package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ValidationTest {
    private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 10, 3);
    private static final LocalDate UN_CORRECT_DATE_OF_BIRTHDAY = LocalDate.of(3990, 12, 31);
    private static final LocalDate UN_CORRECT_DATE_RELISE = LocalDate.of(1000, 12, 3);
    private static final Duration CORRECT_DURATION = Duration.ofMinutes(120);
    private static Validator validator;

    @BeforeAll
    static void buildValidator() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    @Test
    void user_validation_correct() {
        User user = User.builder().email("aaa@mail.com").birthday(CORRECT_DATE).build();
        assertNotEquals(0, validator.validate(user).size(),
                "не корректная валидация по пустому логину");

        User user1 = User.builder().email("bbb@mail.com").login("login login").birthday(CORRECT_DATE).build();
        assertNotEquals(0, validator.validate(user1).size(),
                "не корректная валидация по логину с пробелами");

        User user2 = User.builder().login("login").birthday(CORRECT_DATE).build();
        assertNotEquals(0, validator.validate(user2).size(),
                "не корректная валидация по пустой почте");

        User user3 = User.builder().email("это-неправильный?эмейл@").login("login").birthday(CORRECT_DATE).build();
        assertNotEquals(0, validator.validate(user3).size(),
                "не корректная валидация по формату почты");

        User user4 = User.builder().email("ccc@mail.com").login("login")
                .birthday(UN_CORRECT_DATE_OF_BIRTHDAY).build();
        assertNotEquals(0, validator.validate(user4).size(),
                "не корректная валидация по неверной дате");

        User user5 = User.builder().email("aaa@mail.com").login("login").name("Mortie")
                .birthday(CORRECT_DATE).build();
        assertEquals(0, validator.validate(user5).size(), "отсеивается корректный пользователь");
    }

    @Test
    void film_validation_correct() {
        Film film = Film.builder().name("AAA BBB CCC").description("description").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).build();
        assertEquals(0, validator.validate(film).size(), "отсеивается корректный фильм");

        Film film1 = Film.builder().description("description").releaseDate(CORRECT_DATE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).build();
        assertNotEquals(0, validator.validate(film1).size(),
                "не корректная валидация по пустому имени");

        Film film2 = Film.builder().name("AAA BBB CCC")
                .description("KgIGmWllKP2ysubdsPcJelTnLe08qxRZ7fYQ6B5ISLOgJxnxw9qA4B7FMexiTDoqGGJenXN9D8KaGwgGg0onl" +
                        "VrADNnHi9PUAV4XPJsafP09pTYy4HUTYoe3Ju2SDIYvZfGemqskAWuASKlNoKUTYva31VzYp7ukuvSJf8x7PsQ" +
                        "ddfh7mzxcUmBPY7tZtrcD4IGh6Qe4GqyT0qBMAxPJf6voqGOweOkOMCSE406JsZ3FIRMsPa87Uhp")
                .releaseDate(CORRECT_DATE).duration(CORRECT_DURATION).rate(10)
                .mpa(new Pair(1, "G")).build();
        assertNotEquals(0, validator.validate(film2).size(),
                "не корректная валидация при количестве символов более 200 в описании");

        Film film3 = Film.builder().name("AAA BBB CCC").description("description").releaseDate(CORRECT_DATE)
                .duration(Duration.ZERO).rate(10).mpa(new Pair(1, "G")).build();
        assertNotEquals(0, validator.validate(film3).size(),
                "Некорректная валидация при длительности ноль");

        Film film4 = Film.builder().name("AAA BBB CCC").description("description").releaseDate(UN_CORRECT_DATE_RELISE)
                .duration(CORRECT_DURATION).rate(10).mpa(new Pair(1, "G")).build();
        assertNotEquals(0, validator.validate(film4).size(),
                "Некорректная валидация неверной даты релиза");
    }
}
