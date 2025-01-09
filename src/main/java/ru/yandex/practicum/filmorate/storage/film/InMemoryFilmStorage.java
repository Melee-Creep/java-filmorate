package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new LinkedHashMap<>();
    private static final LocalDate MIN_DATE_RELEASE_FILM = LocalDate.of(1895, 12, 28);
    private final Map<Film, Set<User>> likeFilms = new HashMap<>();


    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Collection<Film> findPopular(int count) {

    return likeFilms.entrySet().stream()
            .sorted((film1, film2) -> Integer.compare(film2.getValue().size(), film1.getValue().size()))
            .limit(count)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public Film findById(long id) {
        Optional<Film> film = Optional.ofNullable(films.get(id));
        return film.orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    @Override
    public Film create(Film film) {
        String method = "Post";
        validateFilm(film, method);
        likeFilms.put(film, Set.of());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        String method = "Put";
        validateFilm(newFilm, method);
        return newFilm;
    }

    @Override
    public void addLike(long id, User user) {
        Film film = findById(id);
        // если фильм ещё не лайкали
        if (likeFilms.get(film) == null || likeFilms.get(film).isEmpty()) {
            likeFilms.put(film, Set.of(user));
            log.info("фильм лайнули первый раз");
            log.info(likeFilms.toString());
            return;
       }
        Set<User> updateUserLikes = new HashSet<>(likeFilms.get(film));
        log.info("updateUserLikes= " + updateUserLikes);
        log.info("юзер для обновы= " + user);
        updateUserLikes.add(user);
        log.info("Обновили updateUserLikes= " + updateUserLikes);
        likeFilms.put(film, updateUserLikes);
        log.info("добавили лайк");
        log.info(likeFilms.toString());
    }

    @Override
    public void deleteLike(long id, User user) {
        Film film = findById(id);
        Set<User> updateUserLikes = new HashSet<>(likeFilms.get(film));
        updateUserLikes.remove(user);
        likeFilms.put(film, updateUserLikes);
        log.info("удалили лайк = " +  likeFilms.get(film).toString());
    }

    private void validateFilm(Film film, String method) {

        switch (method) {
            case "Post":
                if (film.getName() == null || film.getName().isBlank()) {
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
