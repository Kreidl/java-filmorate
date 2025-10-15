package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    FilmDto createFilm(Film film);

    FilmDto updateFilm(Film updatedFilm);

    void deleteFilm(Long id);

    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(Long filmId);

    List<Film> getTopPopularFilms(int count);

    boolean isContains(Long id);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    public List<Long> getAllFilmLikes(Long filmId);
}
