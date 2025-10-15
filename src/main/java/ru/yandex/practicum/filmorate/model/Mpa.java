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
public enum Mpa {
    G(1, "G"), //У фильма нет возрастных ограничений
    PG(2, "PG"), //Детям рекомендуется смотреть фильм с родителями
    PG13(3, "PG-13"), //Детям до 13 лет просмотр не желателен
    R(4, "R"), //Лицам до 17 лет просматривать фильм можно только в присутствии взрослого
    NC17(5, "NC-17"); //Лицам до 18 лет просмотр запрещён

    @NotNull(message = "Id рейтинга MPA не может быть пустым.")
    @Min(value = 1, message = "Значение id не может быть меньше 1.")
    private final int id;

    @NotNull(message = "Название рейтинга MPA не может быть пустым.")
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    public static Mpa mpaRatingById(int id) {
        return Arrays.stream(values())
                .filter(mpaRating -> mpaRating.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Заданный id Mpa-рейтинга не существует."));
    }

    @JsonCreator
    public static Mpa fromJson(Map<String, Object> json) {
        Integer id = (Integer) json.get("id");
        return mpaRatingById(id);
    }
}
