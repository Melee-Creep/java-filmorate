package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User findUserById(long id);

    Collection<User> findUserFriends(long userId);

    Collection<User> getCommonFriends(long userId, long friendId);

    User create(User user);

    User update(User newUser);

    User addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);
}
