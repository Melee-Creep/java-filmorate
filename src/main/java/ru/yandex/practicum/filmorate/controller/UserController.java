package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        String method = "Post";
        validateUser(user, method);
        log.info("Create User: {} - Finished", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Update User: {} - Started", newUser);
        String method = "Put";
        validateUser(newUser, method);
        log.info("Update User: {} - Finished", newUser);
        return newUser;
    }

    private void validateUser(User user, String method) {

        switch (method) {
            case "Post" :
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
                users.put(user.getId(), user);
                break;

            case "Put":
                if (user.getId() == null) {
                    log.error("Не указан id для обновления");
                    throw new NotFoundException("Не указан id для обновления");
                }

                if (users.containsKey(user.getId())) {
                    User oldUser = users.get(user.getId());
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
                    oldUser.setId(user.getId());
                    oldUser.setName(user.getName());
                    oldUser.setEmail(user.getEmail());
                    oldUser.setBirthday(user.getBirthday());
                    oldUser.setLogin(user.getLogin());
                    break;
                }
                log.error("Пользователь с таким id не найден");
                throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
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
}
