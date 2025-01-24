package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

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
        if (film.getMpa() != null && mpaStorage.getMpaById(film.getMpa().getId()).isEmpty()) {
            throw new ValidationException("Рейтинга с id = " + film.getMpa().getId() + " не найден");
        }
        // Проверка на существование жанров
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Жанр с id = " + genre.getId() + " не найден");
                }
            }
        }
        // Фильтрация дублирующихся жанров
        List<Genre> uniqueGenres = film.getGenres().stream()
                .distinct()
                .collect(Collectors.toList());

        film.setGenres(uniqueGenres);
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        findById(newFilm.getId());
        return filmStorage.update(newFilm);
    }

    @Override
    public void addLike(long id, long userId) {
      User user = userStorage.findUserById(userId)
              .orElseThrow(() -> new NotFoundException("Пользователя с таким айди нету"));
      filmStorage.addLike(id, user);
    }

    @Override
    public void deleteLike(long id, long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким айди нету"));
        filmStorage.deleteLike(id, user);
    }
}
