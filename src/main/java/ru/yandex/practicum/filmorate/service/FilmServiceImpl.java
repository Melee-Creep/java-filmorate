package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Collection<Film> findPopular(int count) {
        return filmStorage.findPopular(count);
    }

    @Override
    public Film findById(long id) {
        return filmStorage.findById(id);
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @Override
    public void addLike(long id, long userId) {
      User user = userStorage.findUserById(userId);
      filmStorage.addLike(id, user);
    }

    @Override
    public void deleteLike(long id, long userId) {
        User user = userStorage.findUserById(userId);
        filmStorage.deleteLike(id, user);
    }
}
