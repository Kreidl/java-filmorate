package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MpaRating {
    G(1, "G"), //У фильма нет возрастных ограничений
    PG(2, "PG"), //Детям рекомендуется смотреть фильм с родителями
    PG13(3, "PG-13"), //Детям до 13 лет просмотр не желателен
    R(4, "R"), //Лицам до 17 лет просматривать фильм можно только в присутствии взрослого
    NC17(5, "NC-17"); //Лицам до 18 лет просмотр запрещён

    @NotBlank(message = "Id рейтинга MPA не может быть пустым.")
    @Min(value = 1, message = "Значение id не может быть меньше 1.")
    private final int id;

    @NotBlank(message = "Название рейтинга MPA не может быть пустым.")
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
