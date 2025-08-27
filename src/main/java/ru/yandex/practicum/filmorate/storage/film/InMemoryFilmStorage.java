package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        filmDateReleaseValidation(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Новый фильм успешно добавлен.");
        return film;
    }

    @Override
    public Film update(Film updatedFilm) {
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

    @Override
    public void delete(Film film) {
        if (film.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (films.containsKey(film.getId())) {
            films.remove(film);
            log.info("Фильм успешно удалён.");
        }
        log.error("Пользователь ввёл несуществующий Id.");
        throw new NotFoundException("Такого фильма не существует.");
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(long filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Пользователь ввёл несуществующий id фильма.");
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        return films.get(filmId);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
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
