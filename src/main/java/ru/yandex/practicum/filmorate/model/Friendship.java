package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friendship {
    @NotBlank(message = "Id пользователя не может быть пустым.")
    @Min(value = 1, message = "Id пользователя не может быть меньше 1.")
    private long id;

    @NotBlank(message = "Id пользователя не может быть пустым.")
    @Min(value = 1, message = "Id пользователя не может быть меньше 1.")
    private long friendId;

    @NotNull
    private boolean isFriend;
}
