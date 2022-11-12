package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.util.DurationConverter;
import ru.yandex.practicum.filmorate.util.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Getter
public class Film {
    private Integer id;
    @NotNull
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @NotNull
    @ReleaseDateValidation
    private final LocalDate releaseDate;
    @DurationMin(minutes = 1)
    @JsonSerialize(using = DurationConverter.class)
    private final Duration duration;
    @NotNull
    private final int rate;
    private final List<Pair> genres;
    private final Pair mpa;

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                Duration duration, int rate, List<Pair> genres, Pair mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.genres = genres;
        this.mpa = mpa;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return getRate() == film.getRate() && Objects.equals(getId(), film.getId())
                && Objects.equals(getName(), film.getName())
                && Objects.equals(getDescription(), film.getDescription())
                && Objects.equals(getReleaseDate(), film.getReleaseDate())
                && Objects.equals(getDuration(), film.getDuration())
                && Objects.equals(getMpa(), film.getMpa())
                && Objects.equals(getGenres(), film.getGenres());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getReleaseDate(),
                getDuration(), getRate(), getMpa(), getGenres());
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", rate=" + rate +
                ", mpas=" + mpa +
                ", genre=" + genres +
                '}';
    }
}
