package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserService userService;

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
        user = fillEmptyUserName(user);
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

    private User fillEmptyUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }
}
