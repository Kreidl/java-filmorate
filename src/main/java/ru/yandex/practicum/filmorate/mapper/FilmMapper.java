package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        log.debug("Начало конвертации запроса в объект класса Film.");
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setGenres(Optional.ofNullable(request.getGenres())
                .orElseGet(Collections::emptySet)
                .stream()
                .sorted(Comparator.comparing(GenreDtoFilm::getId))
                .map(dto -> Genre.genreById(dto.getId()))
                .collect(Collectors.toSet())
        );
        film.setMpa(Mpa.mpaRatingById(request.getMpa().getId()));
        log.debug("Окончание конвертации запроса в объект класса Film.");
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        log.debug("Начало конвертации объекта Film в объект класса FilmDto.");
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setGenres(film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .map(GenreMapper::mapToGenreDto)
                .toList()
        );
        filmDto.setMpa(new MpaDto(film.getMpa().getId(), film.getMpa().getName()));
        log.debug("Окончание конвертации объекта Film в объект класса FilmDto.");
        return filmDto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        log.debug("Начало обновления полей объекта Film из запроса.");
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres().stream()
                    .map(genre -> Genre.genreById(genre.getId()))
                    .collect(Collectors.toSet()));
        }
        if (request.hasMpaRating()) {
            film.setMpa(Mpa.mpaRatingById(request.getMpa().getId()));
        }
        log.debug("Окончание обновления полей объекта Film из запроса.");
        return film;
    }
}
