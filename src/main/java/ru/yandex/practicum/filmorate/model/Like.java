package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Like {
    private final int filmId;
    private final int userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return getFilmId() == like.getFilmId() && getUserId() == like.getUserId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilmId(), getUserId());
    }
}
