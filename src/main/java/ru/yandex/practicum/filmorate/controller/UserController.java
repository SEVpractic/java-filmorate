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
    public List<User> get() {
        return userService.getAll();
    }

    @GetMapping(path = "/{id}")
    public User getByID(@PathVariable ("id") int userID) {
        return userService.getByID(userID);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user = fillEmptyUserName(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
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
    public List<User> getCommonFriends(@PathVariable ("id") int userID,
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
