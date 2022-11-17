package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

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
    public List<Film> fillFilmsByGenres(List<Film> films) {
        String sqlQuery = "SELECT gf.film_id, g.genre_id, g.genre_name " +
                "FROM genres_film AS gf " +
                "INNER JOIN genres AS g ON gf.genre_id = g.genre_id ";

        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlQuery);
        HashMap<Integer, List<Pair>> genresByFilms;
        try {
            genresByFilms = makeGenres(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return films.stream()
                .map(g -> g = g.toBuilder().genres(genresByFilms.get(g.getId())).build())
                .collect(Collectors.toList());
    }

    @Override
    public void createGenres(int filmId, List<Pair> genres) {
        String sqlQuery = "INSERT INTO genres_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
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
        log.info("удалены жанры фильма с ID №{}", filmId);
    }

    private Pair makeGenre(ResultSet rs) throws SQLException {
        return new Pair(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }

    private HashMap<Integer, List<Pair>> makeGenres(SqlRowSet rs) throws SQLException {
        HashMap<Integer, List<Pair>> genresByFilms = new HashMap<>();

        while (rs.next()) {
            int i = rs.getInt("film_id");
            Pair genre = new Pair(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            );

            if (genresByFilms.containsKey(i)) {
                genresByFilms.get(i).add(genre);
            } else {
                genresByFilms.put(i, new ArrayList<>());
                genresByFilms.get(i).add(genre);
            }
        }

        return genresByFilms;
    }
}
