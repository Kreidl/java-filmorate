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
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Проверка добавления нового фильма с корректными данными")
    void createFilmTest() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        filmController.create(film);
        assertNotNull(filmController.findAll(), "Фильм не добавлен в список фильмов");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректным названием")
    void validateFilmWithoutNameTest() {
        Film film = Film.builder()
                .name("")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Название фильма не может быть пустым.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной длиной описания")
    void validateFilmWithIncorrectLengthOfDescription() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("a".repeat(201))
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в названии фильма проигнорирована");
        assertEquals("Описание фильма должно быть от 1 до 200 символов.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной продолжительностью")
    void validateFilmWithIncorrectDurationTest() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(-20)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в продолжительности фильма проигнорирована");
        assertEquals("Продолжительность фильма не может быть нулевой или отрицательной.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
        film.setDuration(0);
        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка в продолжительности фильма проигнорирована");
        assertEquals("Продолжительность фильма не может быть нулевой или отрицательной.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации фильма с некорректной датой релиза")
    void createFilmWithIncorrectDateReleaseTest() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(1800, 10, 10))
                .duration(120)
                .build();
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(0, filmController.findAll().size(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
    @DisplayName("Проверка обновления фильма с корректными данными")
    void updateFilmTest() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        filmController.create(film);
        Film updatedFilm = Film.builder()
                .name("Обновлённый фильм 1")
                .description("Обновлённое описание фильма 1")
                .releaseDate(film.getReleaseDate())
                .duration(120)
                .build();
        updatedFilm.setId(film.getId());
        filmController.update(updatedFilm);
        assertNotNull(filmController.findAll(), "Фильм не добавлен в список фильмов");
        assertEquals(updatedFilm.getName(), film.getName(), "Фильм не обновлён");
    }

    @Test
    @DisplayName("Проверка обновления даты релиза фильма на некорректную")
    void updateFilmWithIncorrectDateReleaseTest() {
        Film film = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        filmController.create(film);
        Film updatedFilm = Film.builder()
                .name("Обновлённый фильм 1")
                .description("Обновлённое описание фильма 1")
                .releaseDate(LocalDate.of(1800, 10, 10))
                .duration(120)
                .build();
        updatedFilm.setId(film.getId());
        assertThrows(ValidationException.class, () -> {
            filmController.update(updatedFilm);
        });
        assertNotEquals(film.getReleaseDate(), updatedFilm.getReleaseDate(), "Даты релиза фильмов одинаковые");
    }

    @Test
    @DisplayName("Проверка корректного возвращения всех фильмов")
    void findAllFilmsTest() {
        Film film1 = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .duration(120)
                .build();
        Film film2 = Film.builder()
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(150)
                .build();
        filmController.create(film1);
        filmController.create(film2);
        HashMap<Integer, Film> films = new HashMap<>();
        films.put(1, film1);
        films.put(2, film2);
        assertEquals(films.values().toString(), filmController.findAll().toString(), "Список фильмов некорректный");
    }
}