package ru.yandex.practicum.filmorate.util.makers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(Duration.ofSeconds(rs.getInt("duration")))
                .rate(rs.getInt("rate"))
                .mpa(new Pair(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();
    }
}
