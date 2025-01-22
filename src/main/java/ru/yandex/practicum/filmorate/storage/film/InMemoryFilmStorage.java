package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new LinkedHashMap<>();
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
        if (film.getId() == null) {
            log.info("Начало присвоения id фильму: {} id = {}", film.getName(), film.getId());
            film.setId(getNextId());
            log.info("Конец присвоения id фильму: {} id = {}", film.getName(), film.getId());
        }
        films.put(film.getId(), film);
        likeFilms.put(film, Set.of());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Не указан id для обновления");
            throw new NotFoundException("Не указан id для обновления");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return newFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
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
