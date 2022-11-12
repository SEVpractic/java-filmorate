package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
public class Friend {
    @NonNull
    private final int friendId;
    @NonNull
    private final boolean isConfirmed;

    public Friend(int friendId, boolean isConfirmed) {
        this.friendId = friendId;
        this.isConfirmed = isConfirmed;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendId=" + friendId +
                ", isConfirmed=" + isConfirmed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return getFriendId() == friend.getFriendId() && isConfirmed() == friend.isConfirmed();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFriendId(), isConfirmed());
    }
}
