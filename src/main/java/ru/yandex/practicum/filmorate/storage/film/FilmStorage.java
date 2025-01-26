package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    List<Film> findPopular(int count);

    Film findById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(long id, User user);

    void deleteLike(long id, User user);
}
