package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilm;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmDto createFilm(NewFilmRequest request) {
        log.debug("Начало добавления фильма {}", request);
        validateReleaseDate(request.getReleaseDate());
        if (request.getMpa() != null) {
            validateMpaRating(request.getMpa());
        }
        if (request.getGenres() != null) {
            validateGenres(request.getGenres());
        }
        validateDuration(request.getDuration());
        Film film = mapToFilm(request);
        FilmDto filmDto = filmStorage.createFilm(film);
        log.info("Фильм {} добавлен.", film);
        return filmDto;
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        log.debug("Начало обновления фильма {}", request);
        if (request.getReleaseDate() != null) {
            validateReleaseDate(request.getReleaseDate());
        }
        if (request.getMpa() != null) {
            validateMpaRating(request.getMpa());
        }
        if (request.getGenres() != null) {
            validateGenres(request.getGenres());
        }
        if (request.getDuration() != null) {
            validateDuration(request.getDuration());
        }
        if (request.getId() == null) {
            Film film = new Film();
            FilmMapper.updateFilmFields(film, request);

            return FilmMapper.mapToFilmDto(film);
        }
        if (!filmStorage.isContains(request.getId())) {
            log.error("Фильм с id = {} не найден.", request.getId());
            throw new NotFoundException("Фильм с id = " + request.getId() +" не найден.");
        }
        Film updatedFilm = filmStorage.getFilmById(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> {
                    log.error("Фильм с id = {} не найден.", request.getId());
                    return new NotFoundException("Фильм с id = " + request.getId() +" не найден.");
                });
        FilmDto filmDto = filmStorage.updateFilm(updatedFilm);
        log.info("Фильм {} обновлён.", updatedFilm);
        return filmDto;
    }

    public void deleteFilm(Long id) {
        log.debug("Начало удаления фильма с id {}.", id);
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + "не найден.");
        }
        filmStorage.deleteFilm(id);
        log.info("Фильм с id {} удалён.", id);
    }

    public FilmDto getFilmById(Long filmId) {
        log.debug("Получаем фильм с id {}.", filmId);
        return filmStorage.getFilmById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> {
                    log.error("Фильм с id = {} не найден.", filmId);
                    return new NotFoundException("Фильм с id = " + filmId +" не найден.");
                });
    }

    public Collection<FilmDto> getAllFilms() {
        log.debug("Получаем список всех фильмов.");
        return filmStorage.getAllFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Начало добавления лайка фильму с id {} от пользователя с id {}", filmId, userId);
        if (!userStorage.isContains(userId)) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!filmStorage.isContains(filmId)) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        if (getAllFilmLikes(filmId).contains(userId)) {
            log.warn("Лайк от пользователя с id={} фильму с id={} уже поставлен.", userId, filmId);
            throw new DuplicateException("Лайк от пользователя с id=" + userId + " фильму с id=" + filmId + " уже поставлен.");
        } else {
            filmStorage.addLike(filmId, userId);;
            log.info("Лайк от пользователя с id={} фильму с id={} поставлен.", userId, filmId);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!userStorage.isContains(userId)) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        if (!filmStorage.isContains(filmId)) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        if (!getAllFilmLikes(filmId).contains(userId)) {
            log.warn("Лайк от пользователя с id={} фильму с id={} не поставлен.", userId, filmId);
            throw new NotFoundException("Лайк от пользователя с id=" + userId + " фильму с id=" + filmId + " не поставлен.");
        } else {
            filmStorage.deleteLike(filmId, userId);
            log.info("Лайк от пользователя с id={} фильму с id={} удалён.", userId, filmId);
        }
    }

    public List<Long> getAllFilmLikes(Long filmId) {
        if (!filmStorage.isContains(filmId)) {
            log.error("Фильм с id = {} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден.");
        }
        return filmStorage.getAllFilmLikes(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        log.debug("Получаем топ популярных фильмов в количестве {} шт.", count);
        return filmStorage.getTopPopularFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void validateReleaseDate(LocalDate date) {
        log.debug("Проверка валидации даты релиза фильма");
        LocalDate earlyDateRelease = LocalDate.of(1895, Month.DECEMBER, 28);
        if (date.isBefore(earlyDateRelease)) {
            log.error("Ошибка при добавлении фильма. Дата релиза не может быть до 28.12.1895.");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    private void validateDuration(int duration) {
        log.debug("Проверка валидации продолжительности фильма");
        if (duration < 0) {
            log.error("Продолжительность фильма должна быть положительной.");
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }

    private void validateMpaRating(MpaDto mpa) {
        log.debug("Проверка валидации Mpa-рейтинга фильма");
        if (mpa.getId() < 1 || mpa.getId() > 5) {
            log.error("Рейтинга Mpa с идентификатором id={} не существует.", mpa.getId());
            throw new NotFoundException("Рейтинга Mpa с идентификатором id=" + mpa.getId() + " не существует.");
        }
    }

    private void validateGenres(Set<GenreDtoFilm> genres) {
        log.debug("Проверка валидации жанров фильма");
        genres.forEach(genre -> {
            if (genre.getId() < 1 || genre.getId() > 6) {
                log.error("Жанра с идентификатором id={} не существует.", genre.getId());
                throw new NotFoundException("Жанра с идентификатором id=" + genre.getId() + " не существует.");
            }
        });
    }
}
