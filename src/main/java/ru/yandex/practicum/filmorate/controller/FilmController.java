package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов.");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма.");
        filmDateReleaseValidation(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм успешно добавлен.");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) {
        log.info("Запрос на обновление данных фильма.");
        if (updatedFilm.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (films.containsKey(updatedFilm.getId())) {
            filmDateReleaseValidation(updatedFilm.getReleaseDate());
            Film oldFilm = films.get(updatedFilm.getId());
            oldFilm.setName(updatedFilm.getName());
            oldFilm.setDescription(updatedFilm.getDescription());
            oldFilm.setDuration(updatedFilm.getDuration());
            oldFilm.setReleaseDate(updatedFilm.getReleaseDate());
            log.info("Данные фильма успешно обновлены.");
            return oldFilm;
        }
        log.error("Пользователь ввёл несуществующий Id.");
        throw new NotFoundException("Такого фильма не существует.");
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void filmDateReleaseValidation(LocalDate dateRelease) {
        LocalDate earlyDateRelease = LocalDate.of(1895, Month.DECEMBER, 28);
        if (dateRelease.isBefore(earlyDateRelease)) {
            log.error("Пользователь ввёл некорректную дату релиза фильма.");
            throw new ValidationException("Дата релиза не раньше " + earlyDateRelease.getDayOfMonth()
                    + " " + earlyDateRelease.getMonth() + " " + earlyDateRelease.getYear() + " года.");
        }
    }
}
