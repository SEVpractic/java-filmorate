package ru.yandex.practicum.filmorate.exceptions;

public class CreationFailException extends RuntimeException {
    public CreationFailException(String message) {
        super(message);
    }
}
