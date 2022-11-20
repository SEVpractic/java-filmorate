package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Pair;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.util.makers.GenreMapper;

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
    public List<Pair> getAll() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new GenreMapper());
    }

    @Override
    public Pair get(int genreID){
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"name"});
                    stmt.setInt(1, genreID);
                    return stmt;
                }, new GenreMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Жанр c ID № %s не существует", genreID))
                );
    }

    @Override
    public Set<Pair> getByFilmID(int filmId) {
        String sqlQuery = "SELECT * FROM genres AS g INNER JOIN genres_film AS gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id = ?";

        return new LinkedHashSet<>(jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setInt(1, filmId);
            return stmt;
        }, new GenreMapper()));
    }

    @Override
    public List<Film> fillFilmsByGenres(List<Film> films) {
        Map<Integer, Set<Pair>> genresByFilms;
        String sqlQueryConditions = films.stream().map(f -> f.getId().toString())
                .reduce((a, b) -> a + ", " + b)
                .orElseThrow(() -> new EntityNotExistException("Список фильмов не обнаружен"));
        String sqlQuery = String.format(
                "SELECT gf.film_id, g.genre_id, g.genre_name " +
                "FROM genres_film AS gf " +
                "INNER JOIN genres AS g ON gf.genre_id = g.genre_id " +
                "WHERE gf.film_id IN (%s)", sqlQueryConditions);

        genresByFilms = jdbcTemplate.query(sqlQuery, this::makeGenres);

        return films.stream()
                .map(g -> {
                    assert genresByFilms != null;
                    return g.toBuilder().genres(genresByFilms.get(g.getId())).build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void create(int filmId, Set<Pair> genres) {
        List<Integer> id = genres.stream().map(Pair::getId).collect(Collectors.toList());
        String sqlQuery = "INSERT INTO genres_film (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, id.get(i));
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public void remove(int filmId) {
        String sqlQuery = "DELETE FROM genres_film WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                filmId
        );
        log.info("удалены жанры фильма с ID №{}", filmId);
    }

    private Map<Integer, Set<Pair>> makeGenres(ResultSet rs) throws SQLException {
        Map<Integer, Set<Pair>> genresByFilms = new HashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("film_id");
            Pair genre = new Pair(
                    rs.getInt("genre_id"),
                    rs.getString("genre_name")
            );

            final Set<Pair> genres = genresByFilms.computeIfAbsent(filmId, k -> new LinkedHashSet<>());
            genres.add(genre);
        }

        return genresByFilms;
    }
}
