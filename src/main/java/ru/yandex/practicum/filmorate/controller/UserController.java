package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private  final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Обработка запроса GET users");
        return userService.findAll();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findUserFriends(@PathVariable Long id) {
        log.info("Обработка запроса GET friends");
        return userService.findUserFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public Collection<User> commonFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Create User: {} - Started", user);
        log.info("Create User: {} - Finished", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Update User: {} - Started", newUser);
        log.info("Update User: {} - Finished", newUser);
        return userService.update(newUser);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        userService.deleteFriend(userId, friendId);
    }
}
