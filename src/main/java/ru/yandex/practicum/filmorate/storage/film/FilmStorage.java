package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Film create(Film film);

    public Film update(Film updatedFilm);

    public void delete(Film film);

    public Collection<Film> getAllFilms();

    public Film getFilmById(long filmId);
}
