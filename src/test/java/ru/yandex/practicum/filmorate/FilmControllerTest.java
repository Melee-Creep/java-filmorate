package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @DisplayName("Добавление фильма")
    @Test
    void createFilm() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @DisplayName("Добавление фильма с пустым именем")
    @Test
    void createFilmWithEmptyName() {
        Film film = new Film(1L, " ", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));

    }

    @DisplayName("Добавление фильма с описание больше 200 символов")
    @Test
    void createFilmWithMore200Description() {
        Film film = new Film(1L, "Название фильма", "Сейчас данные можно хранить в памяти приложения" +
                " — так же, как и в случае с менеджером задач. Для этого используйте контроллер." +
                "В следующих спринтах вы узнаете, как правильно хранить данные в долговременном хранилище, " +
                "чтобы они не зависели от перезапуска приложения.",
                LocalDate.of(2000, 10, 10), 10);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Добавление фильма раньше декабря 1895")
    void createFilmWithBefore1895() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(1800, 10, 10), 10);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    @DisplayName("Добавление фильма с отрицательной длительностью")
    void createFilmWithNegativeDuration() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 0);
        Assertions.assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @DisplayName("Обновление фильма")
    @Test
    void updateFilm() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(1L, "Обновленное Название фильма", "обновленное описание",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        filmController.update(newFilm);
        Assertions.assertEquals(newFilm.getName(), filmController.findAll().stream().toList().getFirst().getName());
    }

    @DisplayName("Обновление фильма с пустым именем")
    @Test
    void updateFilmWithEmptyName() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(1L, " ", "обновленное описание",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        Assertions.assertThrows(ValidationException.class, () -> filmController.update(newFilm));
    }

    @DisplayName("Обновление фильма с пустым айди")
    @Test
    void updateFilmWithEmptyId() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(null, " ", "обновленное описание",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(newFilm));
    }

    @DisplayName("Обновление фильма с описанием больше 200 символов")
    @Test
    void updateFilmWithMore200Description() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(1L, "Название фильма", "Сейчас данные можно хранить в памяти приложения" +
                " — так же, как и в случае с менеджером задач. Для этого используйте контроллер." +
                "В следующих спринтах вы узнаете, как правильно хранить данные в долговременном хранилище, " +
                "чтобы они не зависели от перезапуска приложения.",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        Assertions.assertThrows(ValidationException.class, () -> filmController.update(newFilm));
    }

    @DisplayName("Обновление фильма с годом выпуска до 1895")
    @Test
    void updateFilmWithBefore1895() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(1L, "Обновленное Название фильма", "обновленное описание",
                LocalDate.of(1800, 10, 10), 10);
        filmController.create(film);
        Assertions.assertThrows(ValidationException.class, () -> filmController.update(newFilm));
    }

    @DisplayName("Обновление фильма с отрицательной продолжительностью")
    @Test
    void updateFilmWithNegativeDuration() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(1L, "Обновленное Название фильма", "обновленное описание",
                LocalDate.of(2000, 10, 10), 0);
        filmController.create(film);
        Assertions.assertThrows(ValidationException.class, () -> filmController.update(newFilm));
    }

    @DisplayName("Обновление фильма с неверным айди")
    @Test
    void updateFilmWithIncorrectId() {
        Film film = new Film(1L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        Film newFilm = new Film(10L, "Название фильма", "описание",
                LocalDate.of(2000, 10, 10), 10);
        filmController.create(film);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(newFilm));
    }
}


