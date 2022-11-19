package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorResponse {
    private final String error;
}
