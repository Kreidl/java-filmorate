package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    ANIMATION(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    @NotBlank(message = "Id жанра не может быть пустым.")
    @Min(value = 1, message = "Значение id не может быть меньше 1.")
    private final long id;

    @NotBlank(message = "Название жанра не может быть пустым.")
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
