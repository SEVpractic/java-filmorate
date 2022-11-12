package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Pair {
    private final int id;
    private final String name;

    public Pair(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return getId() == pair.getId() && Objects.equals(getName(), pair.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public String toString() {
        return "Pair{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
