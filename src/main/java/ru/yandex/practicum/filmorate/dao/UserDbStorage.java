package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.CreationFailException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
        return users;
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
        if (keyHolder.getKey() == null) throw new CreationFailException("Не удалось создать пользователя");

        return user.toBuilder().id(keyHolder.getKey().intValue()).build();
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";

        int updatedUserId = jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );

        if (updatedUserId == 0) {
            throw new EntityNotExistException(String.format("Пользователь c ID № %s не существует", user.getId()));
        }

        return user;
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
}
