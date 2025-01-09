package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

  private final Map<Long, User> users = new HashMap<>();
  private final Map<Long, Set<User>> userFriends = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findUserById(long id) {
        log.info("проверка существует ли пользователь");
        Optional<User> user = Optional.ofNullable(users.get(id));
        return user.orElseThrow(() -> new NotFoundException("Пользователя с таким айди нету"));
    }

    @Override
    public Collection<User> findUserFriends(long userId) {
        log.info("начало поиска друзей у id - {}", userId);
        if (userFriends.get(userId) == null || userFriends.get(userId).isEmpty()) {
            log.error("userFriends.get(userId) = " + userFriends.get(userId));
            log.error("у пользователя - {} нет друзей", userId);
//            throw new NotFoundException("У данного пользователя ещё нет другей");
            return Set.of();
        }
        log.info("конец поиска друзей у id - {}", userId);
        return userFriends.get(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        log.info("начало поиска общих друзей у {}, друг - {}", userId, friendId);

        if (user.equals(friend)) {
            log.error("Попытка поиска друзей у себя же");
            throw new ValidationException("Нельзя искать общих друзей у себя же");
        }
        if (userFriends.get(userId) == null || userFriends.get(userId).isEmpty()) {
            log.error("у позльзователя - {} нет друзей", userId);
            throw new NotFoundException("Друзья у пользователя не найдены");
        }
        if (userFriends.get(friendId) == null || userFriends.get(friendId).isEmpty()) {
            log.error("у позльзователя - {} нет друзей", friendId);
            throw new NotFoundException("Друзья у запрашиваемого пользователя не найдены");
        }

        Set<User> userFriend = new HashSet<>(userFriends.get(userId));
        Set<User> friendFriends = new HashSet<>(userFriends.get(friendId));
        userFriend.retainAll(friendFriends);

        if (userFriend.isEmpty()) {
            log.warn("у позльзователя - {} нет общих друзей с пользователем {}", friendId, userId);
            throw new NotFoundException("Общие друзья отсутсвуют");
        }

        log.info("конец поиска общих друзей - {}, друг - {}", user, friend);
        return userFriend;
    }

    @Override
    public User create(User user) {
        String method = "Post";
        validateUser(user, method);
        return user;
    }

    @Override
    public User update(User newUser) {
        String method = "Put";
        validateUser(newUser, method);
        return newUser;
    }

    @Override
    public User addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        log.info("начало добавления в друзья: пользователь - {}, друг - {}", user, friend);
        if (user.equals(friend)) {
            log.warn("Нельзя добавить себя в друзья");
            throw new ValidationException("Нельзя добавить себя в друзья");
        }
        //добавили одного друга
        if (!userFriends.containsKey(user.getId())) {
            userFriends.put(user.getId(), Set.of(friend));
            userFriends.put(friend.getId(), Set.of(user));
            log.info("конец добавления в друзья: пользователь - {}, друг - {}", user, friend);
            return friend;
        }
        // добавляем второго
        log.info("начало добавления если уже есть друзья");
        Set<User> userFriend = new HashSet<>(userFriends.get(user.getId()));
        userFriend.add(friend);
        userFriends.put(user.getId(), userFriend);
        // тут надо добавить у друга
        if (!userFriends.containsKey(friend.getId())) {
            userFriends.put(friend.getId(), Set.of(user));
            log.info("конец добавления в друзья: пользователь - {}, друг - {}", user, friend);
            return friend;
        }
        Set<User> friendFriends = new HashSet<>(userFriends.get(friend.getId()));
        friendFriends.add(user);
        userFriends.put(friend.getId(), friendFriends);
        log.info("конец добавления в друзья: пользователь - {}, друг - {}", user, friend);
        return friend;
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
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
