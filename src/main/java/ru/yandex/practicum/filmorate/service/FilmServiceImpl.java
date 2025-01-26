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

import java.util.List;
import java.util.Objects;
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
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public List<Film> findPopular(int count) {
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
            //Ошибка 400, тесты в гитхабе прошли с 404! баг гитхаба?
        }
        // Проверка на существование жанров
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
                    throw new ValidationException("Жанр с id = " + genre.getId() + " не найден");
                    //Ошибка 400, тесты в гитхабе прошли с 404! баг гитхаба?
                }
            }
        }
        // Фильтрация дублирующихся жанров
        List<Genre> uniqueGenres = Objects.requireNonNull(film.getGenres(), "Cписок жанров пустой").stream()
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
              .orElseThrow(() -> new NotFoundException("Пользователя с Id - " + userId + " нету"));
      filmStorage.addLike(id, user);
    }

    @Override
    public void deleteLike(long id, long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с Id - " + userId + " нету"));
        filmStorage.deleteLike(id, user);
    }
}
