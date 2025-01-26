package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    List<Film> findAll();

    List<Film> findPopular(int count);

    Film findById(long id);

    Film create(Film film);

    Film update(Film newFilm);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);
}
