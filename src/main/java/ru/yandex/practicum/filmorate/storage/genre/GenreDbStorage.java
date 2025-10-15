package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.*;

@Slf4j
@Repository
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";

    public GenreDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new GenreRowMapper());
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        log.debug("Получаем жанр с id {}.", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.debug("Получаем список всех жанров.");
        return findMany(FIND_ALL_QUERY);
    }

}
