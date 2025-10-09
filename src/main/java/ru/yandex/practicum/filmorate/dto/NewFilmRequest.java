package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым.")
    @Size(min = 1, max = 200, message = "Описание фильма должно быть от 1 до 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пустой.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пустой.")
    @Positive(message = "Продолжительность фильма не может быть нулевой или отрицательной.")
    private int duration;

    @NotNull(message = "Mpa-рейтинг фильма не может быть пустым.")
    private MpaDto mpa;

    private Set<GenreDtoFilm> genres;
}
