package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserStorage {

    Map<Long, User> userMap = new HashMap<>();
    Map<Long, Set<User>> userFriendIds = new HashMap<>();
}
