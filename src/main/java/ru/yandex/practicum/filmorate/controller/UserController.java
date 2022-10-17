package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Отправлен перечень пользователей");
        return userService.getUsers();
    }

    @GetMapping(path = "/{id}")
    public User getUserByID(@PathVariable ("id") int userID) {
        log.info("Отправлен пользователь ID №{}", userID);
        return userService.getUserByID(userID);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        log.info("Добавлен пользователь {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.info("Обновлен пользователь {}", user);
        return user;
    }

    @PutMapping(path = "/{id}/friends/{friendID}")
    public void addAsFriend(@PathVariable ("id") int userID,
                            @PathVariable int friendID) {
        userService.addAsFriend(userID, friendID);
        log.info("добавлены в друзья пользователи с ID №{} и №{}", userID, friendID);
    }

    @DeleteMapping(path = "/{id}/friends/{friendID}")
    public void removeFromFriends(@PathVariable ("id") int userID,
                                  @PathVariable int friendID) {
        userService.removeFromFriends(userID, friendID);
        log.info("удалены из друзей пользователи с ID №{} и №{}", userID, friendID);
    }

    @GetMapping(path = "/{id}/friends")
    public List<User> getFriendsList(@PathVariable ("id") int userID) {
        log.info("возвращен перечень друзей пользователя с ID №{}", userID);
        return userService.getFriendsList(userID);
    }

    @GetMapping(path = "/{id}/friends/common/{otherID}")
    public List<User> getCommonFriendsList(@PathVariable ("id") int userID,
                                           @PathVariable ("otherID") int anotherUserID) {
        log.info("возвращен перечень общих друзей пользователей с ID №{} и №{}", userID, anotherUserID);
        return userService.getCommonFriendsList(userID, anotherUserID);
    }
}
