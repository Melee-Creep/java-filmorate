package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        return userStorage.findUserById(id);
    }

    @Override
    public Collection<User> findUserFriends(long userId) {
        return userStorage.findUserFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
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
        return userStorage.addFriend(userId, friendId);

    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }
}
