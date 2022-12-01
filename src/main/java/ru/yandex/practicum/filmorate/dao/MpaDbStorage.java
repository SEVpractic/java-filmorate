package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.util.makers.MpaMapper;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Pair get(int mpaId) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";

        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"name"});
                    stmt.setInt(1, mpaId);
                    return stmt;
                }, new MpaMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("MPA c ID № %s не существует", mpaId))
                );
    }

    @Override
    public Pair getByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM mpa AS m INNER JOIN films AS f ON m.mpa_id = f.mpa_id " +
                "WHERE f.film_id = ?";

        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setInt(1, filmId);
            return stmt;
            }, new MpaMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("MPA c ID № %s не существует", filmId))
                );
    }

    @Override
    public List<Pair> getAll() {
        String sqlQuery = "SELECT * FROM mpa";

        return jdbcTemplate.query(sqlQuery, new MpaMapper());
    }
}
