package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

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
    public Film create(@RequestBody Film film) {
        log.info("Create Film: {} - Started", film);
        log.info("Create Film: {} - Finished", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Update Film: {} - Started", newFilm);
        log.info("Update Film: {} - Finished", newFilm);
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
}
