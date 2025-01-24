package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre create(Genre genre) {
        String sql = "INSERT INTO genres (genre_id, name) VALUES (?, ?)";
        jdbcTemplate.update(sql, genre.getId(), genre.getName());
        return genre;
    }

    // Получение всех жанров
    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    // Получение жанра по ID
    @Override
    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreMapper(), id);
        return genres.stream().findFirst();
    }

    // Метод для добавления жанра в таблицу film_genres, если он существует
    public void addFilmGenre(Long filmId, Integer genreId) {
        // Проверка, существует ли жанр с таким id
        Optional<Genre> genre = getGenreById(genreId);

        if (genre.isPresent()) {
            // Если жанр существует, добавляем в таблицу film_genres
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, genreId);
        } else {
            // Если жанр не существует, выбрасываем исключение или логируем
            throw new IllegalArgumentException("Жанр с id = " + genreId + " не найден в базе данных.");
        }
    }
}
