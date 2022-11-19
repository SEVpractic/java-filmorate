package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.CreationFailException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.EntityMaker.makeFilm;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT * FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film getByID(int filmID) {
        String sqlQuery = "SELECT * FROM films AS f INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id WHERE film_id = ?";

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

    @Override
    public Film create(Film film) {
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
        if (keyHolder.getKey() == null) throw new CreationFailException("Не удалось создать пользователя");

        return film.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, " +
                "mpa_id = ? WHERE film_id = ?";

        int updatedFilmId = jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().getSeconds(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updatedFilmId == 0) {
            throw new EntityNotExistException(String.format("Фильм c ID № %s не существует", film.getId()));
        }

        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery =
                "SELECT f.film_id, f.name, f.description, f.release_date, " +
                        "f.duration, f.rate, f.mpa_id, COUNT(l.film_id), " +
                        "m.mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN likes AS l ON f.film_id = l.film_id " +
                "INNER JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
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
