package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Получение всех рейтингов MPA
    public List<Mpa> getAllMpas() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    // Получение MPA по ID
    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, new MpaMapper(), id);
        return mpaList.stream().findFirst();
    }

    public Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }

}
