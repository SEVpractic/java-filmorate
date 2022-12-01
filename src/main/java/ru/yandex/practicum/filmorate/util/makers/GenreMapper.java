package ru.yandex.practicum.filmorate.util.makers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper<Pair> {
    @Override
    public Pair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pair(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }
}
