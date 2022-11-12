package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class GenresDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Pair> getGenresByFilmID(int filmId) {
        String sqlQuery = "SELECT * FROM genres AS g INNER JOIN genres_film AS gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";
        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setInt(1, filmId);
            return stmt;
        }, (rs, rowNum) -> makeGenre(rs))
                .stream()
                .sorted(Comparator.comparingInt(Pair::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pair> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Pair getGenre(int genreID){
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"name"});
                    stmt.setInt(1, genreID);
                    return stmt;
                }, (rs, rowNum) -> makeGenre(rs))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Жанр c ID № %s не существует", genreID))
                );
    }

    @Override
    public void createGenres(int filmId, List<Pair> genres) {
        Set<Pair> pairs = genres.stream().sorted(Comparator.comparingInt(Pair::getId)).collect(Collectors.toSet());
        List<Pair> finalGenres = pairs.stream().collect(Collectors.toList());
        removeGenres(filmId);
        String sqlQuery = "INSERT INTO genres_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, finalGenres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return finalGenres.size();
            }
        });
    }

    @Override
    public void removeGenres(int filmId) {
        String sqlQuery = "DELETE FROM genres_film WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                filmId
        );
        log.info("удалены жанры фильма с ID №{} фильму №{}", filmId);
    }

    private Pair makeGenre(ResultSet rs) throws SQLException {
        return new Pair(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }
}
