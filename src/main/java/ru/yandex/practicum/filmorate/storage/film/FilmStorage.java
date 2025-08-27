package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film updatedFilm);

    void delete(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(long filmId);
}
