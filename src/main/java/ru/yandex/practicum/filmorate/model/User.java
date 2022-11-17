package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @NotNull
    @Email
    private final String email;
    @NotBlank
    @Pattern(regexp = "[^ ]+$")
    private final String login;
    private String name;
    @NotNull
    @PastOrPresent
    private final LocalDate birthday;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId())
                && Objects.equals(getEmail(), user.getEmail())
                && Objects.equals(getLogin(), user.getLogin())
                && Objects.equals(getName(), user.getName())
                && Objects.equals(getBirthday(), user.getBirthday());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getLogin(), getName(), getBirthday());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
