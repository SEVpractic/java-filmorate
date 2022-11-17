package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class Friend {
    @NonNull
    private final int friendId;
    @NonNull
    private final boolean isConfirmed;

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
