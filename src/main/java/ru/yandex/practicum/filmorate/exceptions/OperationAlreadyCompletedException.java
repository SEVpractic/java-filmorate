package ru.yandex.practicum.filmorate.exceptions;

public class OperationAlreadyCompletedException extends RuntimeException {
    public OperationAlreadyCompletedException(String message) {
        super(message);
    }
}
