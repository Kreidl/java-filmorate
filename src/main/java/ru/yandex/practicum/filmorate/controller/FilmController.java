package ru.yandex.practicum.filmorate.controller;

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
    public Film create(@RequestBody Film film) {
        log.info("Запрос на добавление нового фильма.");
        filmNameValidation(film.getName());
        filmDurationValidation(film.getDuration());
        filmDescriptionValidation(film.getDescription());
        filmDateReleaseValidation(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм успешно добавлен.");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film updatedFilm) {
        log.info("Запрос на обновление данных фильма.");
        if (updatedFilm.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (films.containsKey(updatedFilm.getId())) {
            filmNameValidation(updatedFilm.getName());
            filmDurationValidation(updatedFilm.getDuration());
            filmDescriptionValidation(updatedFilm.getDescription());
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

    private void filmNameValidation(String name) {
        if (name.isEmpty() || name.isBlank()) {
            log.error("Пользователь ввёл пустое название фильма.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
    }

    private void filmDurationValidation(int duration) {
        if (duration < 1) {
            log.error("Пользователь ввёл отрицательную или пустую продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или нулевой.");
        }
    }

    private void filmDescriptionValidation(String description) {
        int maxDescriptionFilmLength = 200;
        if (description.length() > maxDescriptionFilmLength) {
            log.error("Пользователь ввёл некорректное описание фильма - длина не может быть {} символов.",
                    description.length());
            throw new ValidationException("Длина описания фильма не должна превышать "
                    + maxDescriptionFilmLength + " символов.");
        }
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
