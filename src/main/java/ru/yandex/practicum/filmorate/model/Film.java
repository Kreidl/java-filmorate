package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private long id;

    @NotNull(message = "Название фильма не может быть пустым.")
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @NotNull(message = "Описание фильма не может быть пустым.")
    @NotBlank(message = "Описание фильма не может быть пустым.")
    @Size(min = 1, max = 200, message = "Описание фильма должно быть от 1 до 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пустой.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пустой.")
    @Positive(message = "Продолжительность фильма не может быть нулевой или отрицательной.")
    private int duration;

    private Set<Long> likes = new HashSet<>();
}
