package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.model.Genre.genreById;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    void getGenreByIdTest() {
        Optional<Genre> genreOpt = genreDbStorage.getGenreById(2);
        assertThat(genreOpt).isPresent()
                .hasValueSatisfying(genre -> assertThat(genre)
                        .isEqualTo(genreById(2)));
    }

    @Test
    void getAllGenresTest() {
        List<Genre> expGenre = List.of(Genre.COMEDY, Genre.DRAMA, Genre.ANIMATION,
                Genre.THRILLER, Genre.DOCUMENTARY, Genre.ACTION);
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertThat(genres).isEqualTo(expGenre);
    }
}