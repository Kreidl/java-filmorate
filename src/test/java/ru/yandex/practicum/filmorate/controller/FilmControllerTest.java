package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    FilmController filmController;

    private static Validator validator;

    @BeforeEach
    void createNewFilmController() {
        filmController = new FilmController();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Проверка добавления нового фильма с корректными данными")
    void createFilmTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        assertNotNull(filmController.getFilms(), "Фильм не добавлен в список фильмов");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректным названием")
    void validateFilmWithoutNameTest() {
        Film film = new Film("", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Название фильма не может быть пустым.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной длиной описания")
    void validateFilmWithIncorrectLengthOfDescription() {
        Film film = new Film("Фильм 1", "a".repeat(201),
                LocalDate.of(2000, 10, 10), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Описание фильма должно быть от 1 до 200 символов.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной продолжительностью")
    void validateFilmWithIncorrectDurationTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), -20);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Продолжительность фильма не может быть нулевой или отрицательной.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
        film.setDuration(0);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Продолжительность фильма не может быть нулевой или отрицательной.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной датой релиза")
    void createFilmWithIncorrectDateReleaseTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
    @DisplayName("Проверка обновления фильма с корректными данными")
    void updateFilmTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        Film updatedFilm = new Film("Обновлённый фильм 1", "Обновлённое описание фильма 1", film.getReleaseDate(), 120);
        updatedFilm.setId(film.getId());
        filmController.update(updatedFilm);
        assertNotNull(filmController.getFilms(), "Фильм не добавлен в список фильмов");
        assertEquals(updatedFilm.getName(), film.getName(), "Фильм не обновлён");
    }

    @Test
    @DisplayName("Проверка обновления даты релиза фильма на некорректную")
    void updateFilmWithIncorrectDateReleaseTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        Film updatedFilm = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), 120);
        updatedFilm.setId(film.getId());
        assertThrows(ValidationException.class, () -> {
            filmController.update(updatedFilm);
        });
        assertNotEquals(film.getReleaseDate(), updatedFilm.getReleaseDate(), "Даты релиза фильмов одинаковые");
    }

    @Test
    @DisplayName("Проверка корректного возвращения всех фильмов")
    void findAllFilmsTest() {
        Film film1 = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        Film film2 = new Film("Фильм 2", "Описание фильма 2", LocalDate.of(2010, 10, 10), 150);
        filmController.create(film1);
        filmController.create(film2);
        HashMap<Integer, Film> films = new HashMap<>();
        films.put(1, film1);
        films.put(2, film2);
        assertEquals(films.values().toString(), filmController.findAll().toString(), "Список фильмов некорректный");
    }
}