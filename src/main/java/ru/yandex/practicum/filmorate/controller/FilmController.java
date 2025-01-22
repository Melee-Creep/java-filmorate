package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private static final LocalDate MIN_DATE_RELEASE_FILM = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Обработка запроса GET films");
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(name = "count",
            required = false, defaultValue = "10") Integer count) {
        return filmService.findPopular(count);
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        validateFilm(film);
        Film createFilm = filmService.create(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createFilm);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        validateFilm(newFilm);
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                        @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не указана");
        }
        if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE_FILM)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() == null) {
            throw new ValidationException("Нужно указать продолжительность фильма");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getDescription().length() > 200) {
            log.error("Название фильма больше 200 символов");
            throw new ValidationException("Описание фильма слишком длинное");
        }
    }
}
