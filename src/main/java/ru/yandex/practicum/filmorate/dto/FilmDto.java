package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание фильма не может быть пустым.")
    @Size(min = 1, max = 200, message = "Описание фильма должно быть от 1 до 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза фильма не может быть пустой.")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть пустой.")
    @Positive(message = "Продолжительность фильма не может быть нулевой или отрицательной.")
    private Integer duration;

    private MpaDto mpa;

    @NotEmpty(message = "Должен быть указан хотя бы один жанр.")
    private List<GenreDto> genres;

}