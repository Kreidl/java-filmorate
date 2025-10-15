package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Arrays;
import java.util.Map;

@AllArgsConstructor
@Getter
@JsonSerialize
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    ANIMATION(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    @NotNull(message = "Id жанра не может быть пустым.")
    @Min(value = 1, message = "Значение id не может быть меньше 1.")
    private final int id;

    @NotNull(message = "Название жанра не может быть пустым.")
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    public static Genre genreById(int id) {
        return Arrays.stream(values())
                .filter(genre -> genre.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Заданный id жанра не существует."));
    }

    @JsonCreator
    public static Genre fromJson(Map<String, Object> json) {
        Integer id = (Integer) json.get("id");
        return genreById(id);
    }
}
