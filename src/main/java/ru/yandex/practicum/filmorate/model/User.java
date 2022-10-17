package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {
    private Integer id;
    @NotNull
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "[^ ]+$")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public boolean addFriend(int friendID) {
        return friends.add(friendID);
    }

    public boolean removeFriend(int friendID) {
        return friends.remove(friendID);
    }
}
