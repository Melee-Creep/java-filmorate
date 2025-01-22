package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User findUserById(long id) {
        return userStorage.findUserById(id).
                orElseThrow(() -> new NotFoundException("Пользователя с таким айди нету"));
    }

    @Override
    public Collection<User> findUserFriends(long userId) {
        return userStorage.findUserFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {

        log.info("начало поиска общих друзей у {}, друг - {}", userId, friendId);

        User user = findUserById(userId);
        User friend = findUserById(friendId);

        if (user.equals(friend)) {
            throw new ValidationException("Нельзя искать общих друзей у себя же");
        }

        Collection<User> commonFriends = userStorage.getCommonFriends(userId, friendId);

        if (commonFriends.isEmpty()) {
            log.warn("Общие друзья у пользователя {} и {} не найдены", userId, friendId);
            throw new NotFoundException("Общие друзья отсутствуют");
        }
        log.info("поиск общих друзей завершён для {} и {}", userId, friendId);
        return commonFriends;
    }

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        return userStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }
}
