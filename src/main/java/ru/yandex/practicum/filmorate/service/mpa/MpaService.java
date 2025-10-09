package ru.yandex.practicum.filmorate.service.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaDto> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings().stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public MpaDto getMpaRatingById(Integer id) {
        return mpaStorage.getMpaRatingById(id)
                .map(MpaMapper::mapToMpaDto)
                .orElseThrow(() -> {
                    log.error("Рейтинга Mpa с идентификатором id={} не существует.", id);
                    return new NotFoundException("Рейтинга Mpa с идентификатором id=" + id + " не существует.");
                });
    }
}
