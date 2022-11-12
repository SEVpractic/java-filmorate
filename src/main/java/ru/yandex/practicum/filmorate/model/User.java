package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
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
    private final List<Friend> friends;

    public User(Integer id, String email, String login, String name, LocalDate birthday, List<Friend> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && getEmail().equals(user.getEmail()) && getLogin().equals(user.getLogin()) && Objects.equals(getName(), user.getName()) && getBirthday().equals(user.getBirthday());
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
                ", friends=" + friends +
                '}';
    }
}
