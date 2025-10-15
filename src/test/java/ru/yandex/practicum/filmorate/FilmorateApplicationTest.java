package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, MpaDbStorage.class, GenreDbStorage.class})
class FilmorateApplicationTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Test
    @DisplayName("Проверка добавления нового пользователя с корректными данными")
    public void testCreateUser() {
        User newUser = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .build();
        newUser.setName("Пользователь");
        Optional<User> userOpt = Optional.of(userStorage.createUser(newUser));
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "example@ex.ru")
                                .hasFieldOrPropertyWithValue("login", "Пользователь1")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000,10,10))
                                .hasFieldOrPropertyWithValue("name", "Пользователь")
                );
    }
}