package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage pairsStorage;

    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.pairsStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getFilmByID(int filmID) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
                    stmt.setInt(1, filmID);
                    return stmt;
                }, (rs, rowNum) -> makeFilm(rs))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Фильм c ID № %s не существует", filmID))
                );
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                Duration.ofSeconds(rs.getInt("duration")),
                rs.getInt("rate"),
                genreStorage.getGenresByFilmID(rs.getInt("film_id")),
                pairsStorage.getMpaByFilmId(rs.getInt("film_id"))
        );
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rate, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, (int) film.getDuration().getSeconds());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() == null) throw new NullPointerException();
        if (film.getGenres() != null) genreStorage.createGenres(keyHolder.getKey().intValue(), film.getGenres());
        log.info("Добавлен фильм id = {}", keyHolder.getKey().intValue());
        return getFilmByID(keyHolder.getKey().intValue());
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmByID(film.getId());
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().getSeconds(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
        );
        if (film.getGenres() != null) {
            genreStorage.createGenres(film.getId(), film.getGenres());
        } else genreStorage.removeGenres(film.getId());

        log.info("Обновлен фильм id = {}", film);
        return getFilmByID(film.getId());
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, f.rate, f.mpa_id, COUNT(l.film_id) " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.film_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"count"});
                    stmt.setInt(1, count);
                    return stmt;
                }, (rs, rowNum) -> makeFilm(rs));
    }
}
