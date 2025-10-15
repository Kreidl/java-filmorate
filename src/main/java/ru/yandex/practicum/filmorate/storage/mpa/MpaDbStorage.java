package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingRowMapper;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings ORDER BY mpa_id";

    public MpaDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new MpaRatingRowMapper());
    }

    @Override
    public Optional<Mpa> getMpaRatingById(Integer id) {
        log.debug("Получаем Mpa-рейтинг с id {}.", id);
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public List<Mpa> getAllMpaRatings() {
        log.debug("Получаем список всех Mpa-рейтингов.");
        return findMany(FIND_ALL_QUERY);
    }
}
