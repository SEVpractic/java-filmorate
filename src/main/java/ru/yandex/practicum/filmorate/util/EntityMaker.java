package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

public class EntityMaker {
    public static Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(Duration.ofSeconds(rs.getInt("duration")))
                .rate(rs.getInt("rate"))
                .mpa(new Pair(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();
    }

    public static User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public static Friend makeFriend(ResultSet rs) throws SQLException {
        return new Friend(
                rs.getInt("friend_id"),
                rs.getBoolean("is_confirmed"));
    }

    public static int makeStatus(List<Friend> friends, int userID, int friendID) {
        // 0 - нет запроса, 1 - запрос отправлен, 2 - имеется только входящий запрос, 3 - запросы подтверждены.
        if (friends.isEmpty()) return 0;
        if (friends.size() == 2) return 3;
        if (friends.get(0).getFriendId() == friendID
                && !friends.get(0).isConfirmed()) return 1;
        if (friends.get(0).getFriendId() == userID
                && !friends.get(0).isConfirmed()) return 2;
        return 0;
    }

    public static Pair makeGenre(ResultSet rs) throws SQLException {
        return new Pair(
                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
    }

    public static Like makeLike(ResultSet rs) throws SQLException {
        return new Like(
                rs.getInt("film_id"),
                rs.getInt("user_id")
        );
    }

    public static Pair makeMpa(ResultSet rs) throws SQLException {
        return new Pair(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        );
    }
}
