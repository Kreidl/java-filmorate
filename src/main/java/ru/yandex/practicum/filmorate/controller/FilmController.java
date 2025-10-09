package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exceptions.ErrorHandler;

import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
@Import(ErrorHandler.class)
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAllFilms() {
        log.info("Запрос на получение списка всех фильмов.");
        return filmService.getAllFilms();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable("id") @Positive Long id) {
        log.info("Запрос на получение фильма с id={}.", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение {} популярных фильмов.", count);
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest request) {
        log.info("Запрос на добавление нового фильма {}.", request);
        return filmService.createFilm(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest request) {
        log.info("Запрос на обновление данных фильма {}.", request);
        return filmService.updateFilm(request);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @Positive Long id, @PathVariable("userId") @Positive Long userId) {
        log.info("Запрос на добавление лайка фильму с id={} от пользователя с id={}.", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @Positive Long id, @PathVariable("userId") @Positive Long userId) {
        log.info("Запрос на удаление лайка к фильму с id={} от пользователя с id={}.", id, userId);
        filmService.deleteLike(id, userId);
    }
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") @Positive Long id) {
        log.info("Запрос на удаление фильма с id {}.", id);
        filmService.deleteFilm(id);
    }

}
