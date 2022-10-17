package ru.yandex.practicum.filmorate.exceptions;

public class EntityNotExistException extends RuntimeException {
    public EntityNotExistException(String message) {
        super(message);
    }
}
