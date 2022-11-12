package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final FriendsStorage friendsStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(FriendsStorage friendsStorage, JdbcTemplate jdbcTemplate) {
        this.friendsStorage = friendsStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUserByID(int userID) {
        String sqlQuery = "SELECT * FROM users AS u WHERE u.user_id = ?";
        return jdbcTemplate.query(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setInt(1, userID);
            return stmt;
            }, (rs, rowNum) -> makeUser(rs))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotExistException(
                        String.format("Пользователь c ID № %s не существует", userID))
                );
    }

    @Override
    public User createUser(User user) {
        fillEmptyUserName(user);
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() == null) throw new NullPointerException();
        log.info("Добавлен пользователь id = {}", keyHolder.getKey().intValue());
        return getUserByID(keyHolder.getKey().intValue());
    }

    @Override
    public User updateUser(User user) {
        getUserByID(user.getId());
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        log.info("Обновлен пользователь {}", user);
        return getUserByID(user.getId());
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

    private void fillEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                friendsStorage.getFriendsList(rs.getInt("user_id"))
        );
    }
}
