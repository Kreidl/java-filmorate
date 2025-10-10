package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmIdGenres;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilmDto;

@Slf4j
@Repository("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT f.*, m.name AS mpa_rating FROM films f LEFT JOIN " +
            "mpa_ratings m ON f.mpa_id = m.mpa_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, m.name AS mpa_rating FROM films f " +
            "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String TOP_POPULAR_QUERY = "SELECT f.*, m.name AS mpa_rating FROM films f " +
            "LEFT JOIN (SELECT film_id, COUNT(DISTINCT user_id) AS likes_count FROM film_likes GROUP BY film_id) l " +
            "ON f.film_id = l.film_id LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id ORDER BY likes_count DESC LIMIT ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES(?, ?)";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_GENRES_QUERY = "SELECT * FROM genres AS g RIGHT JOIN (SELECT genre_id " +
            "FROM film_genres WHERE film_id = ?) AS f ON g.genre_id = f.genre_id ORDER BY genre_id";
    private static final String FIND_ALL_FILMS_GENRES_QUERY = "SELECT * FROM film_genres ORDER BY film_id";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
    private static final String FIND_ALL_LIKES_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ? " +
            "ORDER BY user_id";

    public FilmDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new FilmRowMapper());
    }

    @Override
    public FilmDto createFilm(Film film) {
        log.debug("Создание нового фильма {}.", film);
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        if (film.getGenres() != null) {
            List<Genre> genres = film.getGenres().stream().toList();
            jdbc.batchUpdate(INSERT_GENRE_QUERY, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, film.getId());
                    ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
        log.trace("Фильм {} создан", film);
        return mapToFilmDto(film);
    }

    @Override
    public FilmDto updateFilm(Film updatedFilm) {
        log.debug("Обновление фильма с id {}.", updatedFilm.getId());
        update(UPDATE_QUERY,
                updatedFilm.getName(),
                updatedFilm.getDescription(),
                Date.valueOf(updatedFilm.getReleaseDate()),
                updatedFilm.getDuration(),
                updatedFilm.getMpa().getId(),
                updatedFilm.getId());
        delete(DELETE_GENRE_QUERY, updatedFilm.getId());
        List<Genre> genres = updatedFilm.getGenres().stream().toList();
        jdbc.batchUpdate(INSERT_GENRE_QUERY, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, updatedFilm.getId());
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        log.debug("Фильм с id {} обновлён.", updatedFilm.getId());
        return mapToFilmDto(getFilmById(updatedFilm.getId()).get());
    }

    @Override
    public void deleteFilm(Long id) {
        log.debug("Удаление фильма с id {}.", id);
        delete(DELETE_QUERY, id);
        log.debug("Фильм с id {} удалён.", id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        log.debug("Получение всех фильмов.");
        List<Film> films = findMany(FIND_ALL_QUERY).stream().toList();
        if (!films.isEmpty()) {
            List<FilmIdGenres> filmsGenreIds = jdbc.query(FIND_ALL_FILMS_GENRES_QUERY, new RowMapper<FilmIdGenres>() {
                @Override
                public FilmIdGenres mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FilmIdGenres filmIdGenres = new FilmIdGenres();
                    filmIdGenres.setFilmId(rs.getLong("film_id"));
                    filmIdGenres.setGenreId(rs.getInt("genre_id"));
                    return filmIdGenres;
                }
            });
            int filmIndex = 0;
            for (FilmIdGenres filmsGenreId : filmsGenreIds) {
                if (filmIndex + 1 != filmsGenreId.getFilmId()) {
                    filmIndex = filmIndex + 1;
                }
                films.get(filmIndex).getGenres().add(Genre.genreById(filmsGenreId.getGenreId()));
            }
        }
        log.trace("Список фильмов {}", films);
        return films;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        log.debug("Получение фильма с id {}.", filmId);
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, filmId);
        if (film.isPresent()) {
            Collection<Genre> genres = new HashSet<>(jdbc.query(FIND_GENRES_QUERY, new GenreRowMapper(), filmId));
            film.get().setGenres(new HashSet<>(genres));
        }
        log.debug("Фильм {} получен.", film.get());
        return film;
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        log.debug("Получение {} топ фильмов.", count);
        List<Film> films = findMany(TOP_POPULAR_QUERY, count);
        if (!films.isEmpty()) {
            List<FilmIdGenres> filmsGenreIds = jdbc.query(FIND_ALL_FILMS_GENRES_QUERY, new RowMapper<FilmIdGenres>() {
                @Override
                public FilmIdGenres mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FilmIdGenres filmIdGenres = new FilmIdGenres();
                    filmIdGenres.setFilmId(rs.getLong("film_id"));
                    filmIdGenres.setGenreId(rs.getInt("genre_id"));
                    return filmIdGenres;
                }
            });
            int filmIndex = 0;
            for (FilmIdGenres filmsGenreId : filmsGenreIds) {
                if (filmIndex + 1 != filmsGenreId.getFilmId()) {
                    filmIndex = filmIndex + 1;
                }
                films.get(filmIndex).getGenres().add(Genre.genreById(filmsGenreId.getGenreId()));
            }
        }
        return films;
    }

    @Override
    public boolean isContains(Long id) {
        log.debug("Проверка на наличие в БД фильма с id {}.", id);
        return getFilmById(id).isPresent();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        log.debug("Добавление лайка фильму с id {} от пользователя с id {}.", filmId, userId);
        jdbc.update(INSERT_LIKE_QUERY, filmId, userId);
        return getFilmById(filmId).get();
    }


    @Override
    public Film deleteLike(Long filmId, Long userId) {
        log.debug("Удаление лайка фильму с id {} от пользователя с id {}.", filmId, userId);
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
        return getFilmById(filmId).get();
    }

    @Override
    public List<Long> getAllFilmLikes(Long filmId) {
        List<Long> likes = jdbc.query(FIND_ALL_LIKES_QUERY, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("user_id");
            }
        }, filmId);
        return likes;
    }
}
