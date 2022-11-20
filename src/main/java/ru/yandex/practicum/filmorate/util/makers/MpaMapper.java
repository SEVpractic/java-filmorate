package ru.yandex.practicum.filmorate.util.makers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaMapper implements RowMapper<Pair> {
    @Override
    public Pair mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Pair(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        );
    }
}
