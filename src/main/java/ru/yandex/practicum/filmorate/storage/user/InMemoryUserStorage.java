package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final UserService userService;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<User>> userFriends = new HashMap<>();

    public InMemoryUserStorage(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findUserById(long id) {
        log.info("проверка существует ли пользователь");
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findUserFriends(long userId) {
        userService.findUserById(userId);
        log.info("начало поиска друзей у id - {}", userId);
        if (userFriends.get(userId) == null || userFriends.get(userId).isEmpty()) {
            log.info("конец поиска друзей у id - {}", userId);
            return Set.of();
        }
        log.info("конец поиска друзей у id - {}", userId);
        return userFriends.get(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {

        Set<User> userFriend = new HashSet<>(userFriends.get(userId));
        Set<User> friendFriends = new HashSet<>(userFriends.get(friendId));
        userFriend.retainAll(friendFriends);
        return userFriend;
    }

    @Override
    public User create(User user) {
        if (user.getId() == null) {
            user.setId(getNextId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Не указан id для обновления");
            throw new NotFoundException("Не указан id для обновления");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setId(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setLogin(newUser.getLogin());
            return newUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User addFriend(long userId, long friendId) {

        log.info("Добавление друга начато: userId={}, friendId={}", userId, friendId);
        User user = userService.findUserById(userId);
        User friend = userService.findUserById(friendId);

        if (user.equals(friend)) {
            log.warn("Нельзя добавить себя в друзья");
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        userFriends.computeIfAbsent(user.getId(), id -> new HashSet<>()).add(friend);
        userFriends.computeIfAbsent(friend.getId(), id -> new HashSet<>()).add(user);
        log.info("Добавление друга завершено: userId={}, friendId={}", userId, friendId);
        return friend;
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = userService.findUserById(userId);
        User friend = userService.findUserById(friendId);
        log.info("начало удаления из друзей: пользователь - {}, друг - {}", user, friend);
        if (user.equals(friend)) {
            log.warn("Нельзя удалить себя же из друзе");
            throw new ValidationException("Нельзя удалить себя же из друзей");
        }
        // удаляем друга у юзера
        if (userFriends.get(user.getId()) == null) {
            Set<User> userFriend = new HashSet<>();
            return;
        }
        Set<User> userFriend = new HashSet<>(userFriends.get(user.getId()));
        userFriend.remove(friend);
        userFriends.replace(user.getId(), userFriend);
        // удаляем юзера у друга
        Set<User> friendFriends = new HashSet<>(userFriends.get(friend.getId()));
        friendFriends.remove(user);
        userFriends.replace(friend.getId(), friendFriends);
        log.info("конец удаления из друзей: пользователь - {}, друг - {}", user, friend);
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
