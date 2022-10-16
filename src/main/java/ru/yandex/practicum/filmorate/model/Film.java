package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.util.DurationConverter;
import ru.yandex.practicum.filmorate.util.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDateValidation
    private LocalDate releaseDate;
    @DurationMin(minutes = 1)
    @JsonSerialize(using = DurationConverter.class)
    private Duration duration;
    private final Set<Integer> likes = new HashSet<>();

    public boolean addLike(int friendID) {
        return likes.add(friendID);
    }

    public boolean dellLike(int friendID) {
        return likes.remove(friendID);
    }
}
