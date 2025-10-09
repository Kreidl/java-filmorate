package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.model.Mpa.mpaRatingById;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    @DisplayName("Проверка получения Mpa-рейтинга по id")
    void getMpaRatingByIdTest() {
        Optional<Mpa> mpaOpt = mpaDbStorage.getMpaRatingById(2);
        assertThat(mpaOpt).isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa)
                        .isEqualTo(mpaRatingById(2)));
    }

    @Test
    @DisplayName("Проверка получения всех Mpa-рейтингов")
    void getAllMpaRatingsTest() {
        List<Mpa> expMpa = List.of(Mpa.G, Mpa.PG, Mpa.PG13, Mpa.R, Mpa.NC17);
        List<Mpa> mpas = mpaDbStorage.getAllMpaRatings();
        assertThat(mpas).isEqualTo(expMpa);
    }
}