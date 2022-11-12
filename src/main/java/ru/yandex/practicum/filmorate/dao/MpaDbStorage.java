package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@Slf4j
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Pair getMpa(int mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"name"});
                    stmt.setInt(1, mpaId);
                    return stmt;
                }, (rs, rowNum) -> makeMpa(rs))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("MPA c ID № %s не существует", mpaId))
                );
    }

    @Override
    public Pair getMpaByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM mpa AS m INNER JOIN films AS f ON m.mpa_id = f.mpa_id " +
                "WHERE f.film_id = ?";
        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setInt(1, filmId);
            return stmt;
            }, (rs, rowNum) -> makeMpa(rs))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("MPA c ID № %s не существует", filmId))
                );
    }

    @Override
    public List<Pair> getAllMpa() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeMpa(rs));
    }

    private Pair makeMpa(ResultSet rs) throws SQLException {
        return new Pair(
                rs.getInt("mpa_id"),
                rs.getString("name"));
    }
}
