package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new LinkedHashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Обработка запроса GET users");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Create User: {} - Started", user);

        if (user.getId() == null) {
            log.info("Присвоение id пользователю");
            user.setId(getNextId());
        }

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

        log.info("Create User: {} - Finished", user);
        users.put(user.getId(), user);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Update User: {} - Started", newUser);
        if (newUser.getId() == null) {
            log.error("Не указан id для обновления");
            throw new NotFoundException("Не указан id для обновления");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail().isBlank()) {
                log.error("Почта не может быть пустой");
                throw new ValidationException("Почта не может быть пустой");
            }
            if (!newUser.getEmail().contains("@")) {
                log.error("Почта должна содержать символ @");
                throw new ValidationException("Почта должна содержать символ @");
            }
            if (newUser.getLogin().isBlank()) {
                log.error("Логин не может быть пустым");
                throw new ValidationException("Логин не может быть пустым");
            }
            if (newUser.getLogin().contains(" ")) {
                log.error("Логин не должен содержать пробелы");
                throw new ValidationException("Логин не должен содержать пробелы");
            }
            if (newUser.getBirthday().isAfter(LocalDateTime.now().toLocalDate())) {
                log.error("Дата рождения не может быть в будущем");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            if (newUser.getName() == null) {
                log.info("Присвоение пустому имени. Имя в качестве логина");
                newUser.setName(newUser.getLogin());
            }
            log.info("Update User: {} - Finished", newUser);
            oldUser.setId(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            return oldUser;
        }
        log.error("Пользователь с таким id не найден");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

}
