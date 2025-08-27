package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return filmStorage.create(film);
    }

    public Film update(Film updatedFilm) {
        if (updatedFilm.getLikes() == null) {
            updatedFilm.setLikes(new HashSet<>());
        }
        return filmStorage.update(updatedFilm);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!getAllFilms().contains(getFilmById(filmId))) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        if (getFilmById(filmId).getLikes().contains(userId)) {
            log.warn("Лайк от пользователя с id={} фильму с id={} уже поставлен.", userId, filmId);
            throw new DuplicateException("Лайк от пользователя с id=" + userId + " фильму с id=" + filmId + " уже поставлен.");
        } else {
            getFilmById(filmId).getLikes().add(userId);
            log.info("Лайк от пользователя с id={} фильму с id={} поставлен.", userId, filmId);
        }
    }

    public void deleteLike(long filmId, long userId) {
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!getAllFilms().contains(getFilmById(filmId))) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        if (!getFilmById(filmId).getLikes().contains(userId)) {
            log.warn("Лайк от пользователя с id={} фильму с id={} не поставлен.", userId, filmId);
            throw new NotFoundException("Лайк от пользователя с id=" + userId + " фильму с id=" + filmId + " не поставлен.");
        } else {
            getFilmById(filmId).getLikes().remove(userId);
            log.info("Лайк от пользователя с id={} фильму с id={} удалён.", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .filter((Film film) -> film.getLikes() != null)
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        LocalDate earlyDateRelease = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate().isBefore(earlyDateRelease)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

}
