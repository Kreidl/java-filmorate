package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilmDto;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    Film film1;
    Film film2;
    Film film3;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    void createFilms() {
        film1 = Film.builder()
                .name("Фильм 1")
                .description("Описание фильма 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.G)
                .genres(Set.of((Genre.ANIMATION)))
                .build();
        film2 = Film.builder()
                .name("Фильм 2")
                .description("Описание фильма 2")
                .releaseDate(LocalDate.of(2000, 2, 2))
                .duration(120)
                .mpa(Mpa.PG)
                .genres(Set.of((Genre.ACTION), Genre.THRILLER))
                .build();
        film3 = Film.builder()
                .name("Фильм 3")
                .description("Описание фильма 3")
                .releaseDate(LocalDate.of(2000, 3, 3))
                .duration(120)
                .mpa(Mpa.PG13)
                .genres(Set.of((Genre.COMEDY), Genre.DRAMA))
                .build();
    }

    @Test
    @DisplayName("Проверка добавления нового фильма с корректными данными")
    @DirtiesContext
    void createFilmTest() {
        Optional<FilmDto> filmOpt = Optional.of(filmStorage.createFilm(film1));
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        assertThat(filmOpt)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Фильм 1")
                                .hasFieldOrPropertyWithValue("description", "Описание фильма 1")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1))
                                .hasFieldOrPropertyWithValue("duration", 120)
                );
    }

    @Test
    @DisplayName("Проверка обновления фильма с корректными данными")
    @DirtiesContext
    void updateFilmTest() {
        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Обновлённый фильм 1")
                .description("Обновлённое описание фильма 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.G)
                .genres(Set.of((Genre.ANIMATION)))
                .build();
        FilmDto updatedFilmDto = mapToFilmDto(updatedFilm);
        Optional<FilmDto> filmDtoOpt = Optional.of(filmStorage.updateFilm(updatedFilm));
        assertThat(filmDtoOpt)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).isEqualTo(updatedFilmDto)
                );
    }

    @Test
    @DisplayName("Проверка удаления фильма")
    @DirtiesContext
    void deleteFilmTest() {
        FilmDto film1Dto = filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        filmStorage.deleteFilm(film1.getId());
        assertEquals(0, filmStorage.getAllFilms().size(), "Фильм не удалён");
    }

    @Test
    @DisplayName("Проверка корректного возвращения всех фильмов")
    @DirtiesContext
    void getAllFilmsTest() {
        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        filmStorage.createFilm(film2);
        assertEquals(2, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        filmStorage.createFilm(film3);
        assertEquals(3, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        film1.setId(1L);
        film2.setId(2L);
        film3.setId(3L);
        List<Film> expFilmsDto = new ArrayList<>();
        expFilmsDto.add(film1);
        expFilmsDto.add(film2);
        expFilmsDto.add(film3);
        Collection<Film> filmsDto = filmStorage.getAllFilms();
        assertThat(filmsDto).isEqualTo(expFilmsDto);
    }

    @Test
    @DisplayName("Проверка получения фильма по id")
    @DirtiesContext
    void getFilmByIdTest() {
        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        film1.setId(1L);
        Optional<Film> filmOpt = filmStorage.getFilmById(1L);
        assertThat(filmOpt).isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).isEqualTo(film1));
    }

    @Test
    @DisplayName("Проверка получения наиболее популярных фильмов")
    @DirtiesContext
    void getTopPopularFilmsTest() {
        FilmDto filmDto1 = filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        FilmDto filmDto2 = filmStorage.createFilm(film2);
        assertEquals(2, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        FilmDto filmDto3 = filmStorage.createFilm(film3);
        assertEquals(3, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        User user1 = User.builder()
                .email("example1@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,1,1))
                .name("Пользователь1")
                .build();
        user1 = userDbStorage.createUser(user1);
        User user2 = User.builder()
                .email("example2@ex.ru")
                .login("Пользователь2")
                .birthday(LocalDate.of(2000,2,2))
                .name("Пользователь2")
                .build();
        user2 = userDbStorage.createUser(user2);
        User user3 = user3 = User.builder()
                .email("example3@ex.ru")
                .login("Пользователь3")
                .birthday(LocalDate.of(2000,3,3))
                .name("Пользователь3")
                .build();
        user3 = userDbStorage.createUser(user3);
        filmStorage.addLike(filmDto1.getId(), user1.getId());
        filmStorage.addLike(filmDto2.getId(), user1.getId());
        filmStorage.addLike(filmDto2.getId(), user2.getId());
        filmStorage.addLike(filmDto2.getId(), user3.getId());
        filmStorage.addLike(filmDto3.getId(), user1.getId());
        filmStorage.addLike(filmDto3.getId(), user2.getId());
        List<Film> expPopularFilms = new ArrayList<>();
        expPopularFilms.add(film2);
        expPopularFilms.add(film3);
        expPopularFilms.add(film1);
        List<Film> popularFilms = filmStorage.getTopPopularFilms(3);
        assertThat(popularFilms).isEqualTo(expPopularFilms);
        expPopularFilms.remove(film1);
        popularFilms = filmStorage.getTopPopularFilms(2);
        assertThat(popularFilms).isEqualTo(expPopularFilms);
    }

    @Test
    @DisplayName("Проверка наличия фильма в БД")
    @DirtiesContext
    void isContainsTest() {
        filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        assertEquals(true, filmStorage.isContains(film1.getId()), "Фильма нет в БД");
        assertThatThrownBy(() -> filmStorage.isContains(2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    @DisplayName("Проверка добавления лайка фильму")
    @DirtiesContext
    void addLikeTest() {
        FilmDto filmDto1 = filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        assertEquals(0, filmStorage.getAllFilmLikes(filmDto1.getId()).size(), "У фильма не должно быть лайков");
        User user1 = User.builder()
                .email("example1@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,1,1))
                .name("Пользователь1")
                .build();
        user1 = userDbStorage.createUser(user1);
        filmStorage.addLike(filmDto1.getId(), user1.getId());
        assertEquals(1, filmStorage.getAllFilmLikes(filmDto1.getId()).size(), "Лайк не поставлен");
    }

    @Test
    @DisplayName("Проверка удаления лайка фильму")
    @DirtiesContext
    void deleteLikeTest() {
        FilmDto filmDto1 = filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        assertEquals(0, filmStorage.getAllFilmLikes(filmDto1.getId()).size(), "У фильма не должно быть лайков");
        User user1 = User.builder()
                .email("example1@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,1,1))
                .name("Пользователь1")
                .build();
        user1 = userDbStorage.createUser(user1);
        filmStorage.addLike(filmDto1.getId(), user1.getId());
        assertEquals(1, filmStorage.getAllFilmLikes(filmDto1.getId()).size(), "Лайк не поставлен");
        filmStorage.deleteLike(filmDto1.getId(), user1.getId());
        assertEquals(0, filmStorage.getAllFilmLikes(filmDto1.getId()).size(), "Лайк не удалён");
    }

    @Test
    @DisplayName("Проверка получения лайков фильма")
    @DirtiesContext
    void getAllFilmLikesTest() {
        FilmDto filmDto1 = filmStorage.createFilm(film1);
        assertEquals(1, filmStorage.getAllFilms().size(), "Фильм не добавлен");
        User user1 = User.builder()
                .email("example1@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,1,1))
                .name("Пользователь1")
                .build();
        user1 = userDbStorage.createUser(user1);
        User user2 = User.builder()
                .email("example2@ex.ru")
                .login("Пользователь2")
                .birthday(LocalDate.of(2000,2,2))
                .name("Пользователь2")
                .build();
        user2 = userDbStorage.createUser(user2);
        filmStorage.addLike(filmDto1.getId(), user1.getId());
        filmStorage.addLike(filmDto1.getId(), user2.getId());
        List<Long> expLikes = new ArrayList<>();
        expLikes.add(user1.getId());
        expLikes.add(user2.getId());
        List<Long> likes = filmStorage.getAllFilmLikes(filmDto1.getId());
        assertThat(likes).isEqualTo(expLikes);
    }
}