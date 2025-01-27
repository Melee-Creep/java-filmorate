package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDateTime;
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
        validateUser(user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Update User: {} - Started", newUser);
        validateUser(newUser);
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

    private void validateUser(User user) {
        if (user.getEmail().isBlank()) {
            log.error("Почта не может быть пустой");
            throw new ValidationException("Почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Почта должна содержать символ @");
            throw new ValidationException("Почта должна содержать символ @");
        }
        if (user.getLogin().isBlank()) {
            log.error("Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Логин не должен содержать пробелы");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDateTime.now().toLocalDate())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null) {
            log.info("Присвоение пустому имени. Имя в качестве логина");
            user.setName(user.getLogin());
        }
    }
}
