package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@Slf4j
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addAsFriend(int userID, int friendID, boolean isConfirmed) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sqlQuery,
                userID,
                friendID,
                isConfirmed
        );
        log.info("добавлены в друзья пользователи с ID №{} и №{}", userID, friendID);
    }

    @Override
    public void confirmFriend(int userID, int friendID) {
        String sqlQuery = "UPDATE friends SET is_confirmed = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                true,
                friendID,
                userID
        );
    }

    @Override
    public void deleteFriend(int userID, int friendID) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery);
            stmt.setInt(1, userID);
            stmt.setInt(2, friendID);
            return stmt;
        });
        log.info("удалены из друзей пользователи с ID №{} и №{}", userID, friendID);
    }

    @Override
    public List<Friend> getFriendsList(int userID) {
        String sqlQuery = "SELECT * FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(con -> {
                    PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
                    stmt.setInt(1, userID);
                    return stmt;
                }, (rs, rowNum) -> makeFriend(rs));
    }

    private Friend makeFriend(ResultSet rs) throws SQLException {
        return new Friend(
                rs.getInt("friend_id"),
                rs.getBoolean("is_confirmed"));
    }
}
