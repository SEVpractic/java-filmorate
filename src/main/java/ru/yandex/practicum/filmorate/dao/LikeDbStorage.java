package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmID, int userID) {
        String sqlQuery = "INSERT INTO likes VALUES (?, ?)";
        jdbcTemplate.update(
                sqlQuery,
                filmID,
                userID
        );
        log.info("добавлен лайк пользователя с ID №{} фильму №{}", userID, filmID);
    }

    @Override
    public void removeLike(int filmID, int userID) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                filmID,
                userID
        );
        log.info("удален лайк пользователя с ID №{} фильму №{}", userID, filmID);
    }

    public Like getLike(int filmID, int userID) {
        String sqlQuery = "SELECT * FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setInt(1, filmID);
            stmt.setInt(2, userID);
            return stmt;
        }, (rs, rowNum) -> makeLike(rs))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Like makeLike(ResultSet rs) throws SQLException {
        return new Like(
                rs.getInt("film_id"),
                rs.getInt("user_id")
        );
    }
}
