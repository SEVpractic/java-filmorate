package ru.yandex.practicum.filmorate.util;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, LocalDate> {
    static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(FIRST_RELEASE_DATE);
    }
}
