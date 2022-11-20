package ru.yandex.practicum.filmorate.util.makers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Friend(
                rs.getInt("friend_id"),
                rs.getBoolean("is_confirmed"));
    }
}
