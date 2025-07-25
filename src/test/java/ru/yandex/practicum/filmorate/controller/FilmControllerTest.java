package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController filmController;

    @BeforeEach
    void createNewFilmController() {
        filmController = new FilmController();
    }

    @Test
    void createFilmTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        assertNotNull(filmController.getFilms(), "Фильм не добавлен в список фильмов");
    }

    @Test
    void createFilmWithoutNameTest() {
        Film film = new Film("", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
    void createFilmWithIncorrectLengthOfDescription() {
        Film film = new Film("Фильм 1", "Аааааааааааааааааааааааааааааааааааа ааааааааааааааааааааа" +
                "аааааааааааааааааааааааа ааааааааааааааааа аааааааааааааааа аааааааааааааа ааааааааааааааааааааааа" +
                "ааааааааааааааааааааа ааааааааааааааааааааа аааааааааааааааааааааааа ааааааааааааааааааааааааа ааа",
                LocalDate.of(2000, 10, 10), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
    void createFilmWithIncorrectDateReleaseTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
    void createFilmWithIncorrectDurationTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), -20);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
        Film film1 = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(1800, 10, 10), 0);
        assertThrows(ValidationException.class, () -> {
            filmController.create(film1);
        });
        assertEquals(new HashMap<>(), filmController.getFilms(), "Фильм не должен быть добавлен в список фильмов");
    }

    @Test
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
    void updateFilmWithoutNameTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        Film updatedFilm = new Film("", "Обновлённое описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        updatedFilm.setId(film.getId());
        assertThrows(ValidationException.class, () -> {
            filmController.update(updatedFilm);
        });
        assertNotEquals(film.getName(), updatedFilm.getName(), "Имена фильмов одинаковые");
    }

    @Test
    void updateFilmWithIncorrectLengthOfDescription() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        Film updatedFilm = new Film("Фильм 1", "Аааааааааааааааааааааааааааааааааааа ааааааааааааааааааааа" +
                "аааааааааааааааааааааааа ааааааааааааааааа аааааааааааааааа аааааааааааааа ааааааааааааааааааааааа" +
                "ааааааааааааааааааааа ааааааааааааааааааааа аааааааааааааааааааааааа ааааааааааааааааааааааааа ааа",
                LocalDate.of(2000, 10, 10), 120);
        updatedFilm.setId(film.getId());
        assertThrows(ValidationException.class, () -> {
            filmController.update(updatedFilm);
        });
        assertNotEquals(film.getDescription(), updatedFilm.getDescription(), "Описания фильмов одинаковые");
    }

    @Test
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
    void updateFilmWithIncorrectDurationTest() {
        Film film = new Film("Фильм 1", "Описание фильма 1", LocalDate.of(2000, 10, 10), 120);
        filmController.create(film);
        Film updatedFilm = new Film("Фильм 1", "Описание фильма 1", film.getReleaseDate(), -20);
        updatedFilm.setId(film.getId());
        assertThrows(ValidationException.class, () -> {
            filmController.update(updatedFilm);
        });
        assertNotEquals(film.getDuration(), updatedFilm.getDuration(), "Продолжительности фильмов одинаковые");
    }

    @Test
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