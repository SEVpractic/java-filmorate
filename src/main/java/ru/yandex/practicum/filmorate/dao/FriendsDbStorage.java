package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addAsFriend(int userID, int friendID, boolean isConfirmed) {
        String sqlQuery = "INSERT INTO friends VALUES (?, ?, ?)";

        try {
            jdbcTemplate.update(
                    sqlQuery,
                    userID,
                    friendID,
                    isConfirmed
            );
        } catch (Exception e) {
            throw new EntityNotExistException(String.format("Пользователь c ID № %s не существует", friendID));
        }
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
    public int getFriendsStatus(int userID, int friendID) {
        String sqlQuery = "SELECT * FROM friends WHERE user_id IN (?, ?) AND friend_id IN (?, ?)";

        List<Friend> friends = jdbcTemplate.query((con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setInt(1, userID);
            stmt.setInt(2, friendID);
            stmt.setInt(3, friendID);
            stmt.setInt(4, userID);
            return stmt;
        }), (rs, rowNum) -> makeFriend(rs));

        return makeStatus(friends, userID, friendID);
    }

    @Override
    public List<User> getFriends(int userID) {
        String sqlQuery = "SELECT * FROM users AS u " +
                "INNER JOIN friends AS f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setInt(1, userID);
            return stmt;
        }, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public List<User> getCommonFriends(int userID, int anotherUserID) {
        String sqlQuery = "SELECT * FROM users AS u WHERE u.user_id  IN (" +
                "SELECT a.friend_id FROM friends AS a " +
                "INNER JOIN friends AS b ON a.friend_id = b.friend_id " +
                "WHERE a.user_id = ? AND b.user_id = ?)";

        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setInt(1, userID);
            stmt.setInt(2, anotherUserID);
            return stmt;
        }, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private Friend makeFriend(ResultSet rs) throws SQLException {
        return new Friend(
                rs.getInt("friend_id"),
                rs.getBoolean("is_confirmed"));
    }

    private int makeStatus(List<Friend> friends, int userID, int friendID) {
        // 0 - нет запроса, 1 - запрос отправлен, 2 - имеется только входящий запрос, 3 - запросы подтверждены.
        if (friends.size() == 0) return 0;
        if (friends.size() == 2) return 3;
        if (friends.get(0).getFriendId() == friendID
                && !friends.get(0).isConfirmed()) return 1;
        if (friends.get(0).getFriendId() == userID
                && !friends.get(0).isConfirmed()) return 2;
        return 0;
    }
}
