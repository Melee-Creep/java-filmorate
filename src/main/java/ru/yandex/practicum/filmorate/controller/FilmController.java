package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new LinkedHashMap<>();
    private static final LocalDate MIN_DATE_RELEASE_FILM = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Обработка запроса GET films");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Create Film: {} - Started", film);
        String method = "Post";
        validateFilm(film, method);
        log.info("Create Film: {} - Finished", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Update Film: {} - Started", newFilm);
        String method = "Put";
        validateFilm(newFilm, method);
        log.info("Update Film: {} - Finished", newFilm);
        return newFilm;
    }

    private void validateFilm(Film film, String method) {

        switch (method) {
            case "Post":
                if (film.getName().isBlank()) {
                    log.error("Название фильма пустое");
                    throw new ValidationException("Название фильма не может быть пустым");
                }

                if (film.getDescription().length() > 200) {
                    log.error("Название фильма больше 200 символов");
                    throw new ValidationException("Описание фильма слишком длинное");
                }

                if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE_FILM)) {
                    log.error("Указана дата выпуска до 28.12.1895");
                    throw new ValidationException("Дата выпуска фильма слишком ранняя");
                }

                if (film.getDuration() < 1) {
                    log.error("Продолжительность фильма меньше минуты");
                    throw new ValidationException("Продолжительность фильма меньше минуты");
                }

                if (film.getId() == null) {
                    log.info("Начало присвоения id фильму: {} id = {}", film.getName(), film.getId());
                    film.setId(getNextId());
                    log.info("Конец присвоения id фильму: {} id = {}", film.getName(), film.getId());
                }
                films.put(film.getId(), film);
                break;

            case "Put":
                if (film.getId() == null) {
                    log.error("Не указан id для обновления");
                    throw new NotFoundException("Не указан id для обновления");
                }

                if (films.containsKey(film.getId())) {
                    Film oldFilm = films.get(film.getId());
                    if (film.getName().isBlank()) {
                        log.error("Название фильма пустое");
                        throw new ValidationException("Название фильма не может быть пустым");
                    }
                    if (film.getDescription().length() > 200) {
                        log.error("Название фильма больше 200 символов");
                        throw new ValidationException("Описание фильма слишком длинное");
                    }
                    if (film.getReleaseDate().isBefore(MIN_DATE_RELEASE_FILM)) {
                        log.error("Указана дата выпуска до 28.12.1895");
                        throw new ValidationException("Дата выпуска фильма слишком ранняя");
                    }
                    if (film.getDuration() < 1) {
                        log.error("Продолжительность фильма меньше минуты");
                        throw new ValidationException("Продолжительность фильма меньше минуты");
                    }
                    oldFilm.setDescription(film.getDescription());
                    oldFilm.setName(film.getName());
                    oldFilm.setDuration(film.getDuration());
                    oldFilm.setReleaseDate(film.getReleaseDate());
                    break;
                }
                log.error("Фильм с таким id не найден");
                throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
    }

    // вспомогательный метод для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
