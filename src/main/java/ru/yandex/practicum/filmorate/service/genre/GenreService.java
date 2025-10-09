package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreDto getGenreById(Integer id) {
        return genreStorage.getGenreById(id)
                .map(GenreMapper::mapToGenreDto)
                .orElseThrow(() -> {
                    log.warn("Жанра с id {} не существует.", id);
                    return new NotFoundException("Жанра с id " + id + "не существует.");
                });
    }

    public List<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres()
                .stream()
                .sorted(Comparator.comparing(Genre::getId))
                .map(GenreMapper::mapToGenreDto)
                .toList();
    }

}
