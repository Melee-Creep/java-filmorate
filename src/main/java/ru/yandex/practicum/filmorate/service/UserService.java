package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

public interface UserService {
    User get(long userId);

    User save(User user);

    void update(User user);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);
}
