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
        return userService.getUsers();
    }

    @GetMapping(path = "/{id}")
    public User getUserByID(@PathVariable ("id") int userID) {
        return userService.getUserByID(userID);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(path = "/{id}/friends/{friendID}")
    public void addAsFriend(@PathVariable ("id") int userID,
                            @PathVariable int friendID) {
        userService.addAsFriend(userID, friendID);
    }

    @DeleteMapping(path = "/{id}/friends/{friendID}")
    public void removeFromFriends(@PathVariable ("id") int userID,
                                  @PathVariable int friendID) {
        userService.removeFromFriends(userID, friendID);
    }

    @GetMapping(path = "/{id}/friends")
    public List<User> getFriends(@PathVariable ("id") int userID) {
        return userService.getFriends(userID);
    }

    @GetMapping(path = "/{id}/friends/common/{otherID}")
    public List<User> getCommonFriendsList(@PathVariable ("id") int userID,
                                           @PathVariable ("otherID") int anotherUserID) {
        return userService.getCommonFriends(userID, anotherUserID);
    }
}
