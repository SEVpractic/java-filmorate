package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    public UserController() {
        id = 0;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Отправлен перечень пользователей {}", users);
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        fillEmptyUserName(user);
        updateUserID(user);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (isNotExist(user)) {
            throw new ValidationException("Невозможно обновить. Пользователь " + user + " не существует");
        }

        fillEmptyUserName(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь {}", user);

        return user;
    }

    private void fillEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private boolean isNotExist(User user) {
        return !users.containsKey(user.getId());
    }

    private void updateUserID(User user) {
        id++;
        user.setId(id);
    }
}
