package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private int id;

    @NonNull
    @NotNull(message = "Email пользователя не может быть пустым.")
    @NotBlank(message = "Email пользователя не может быть пустым.")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Пользователь ввёл некорректный Email.")
    @Valid
    private String email;

    @NonNull
    @NotNull(message = "Логин пользователя не может быть пустым.")
    @NotBlank(message = "Логин пользователя не может содержать пробелы или быть пустым.")
    @Pattern(regexp = "\\S+", message = "Логин пользователя не может содержать пробелы или быть пустым.")
    @Valid
    private String login;

    private String name;

    @NonNull
    @NotNull(message = "Дата рождения пользователя не может быть пустой.")
    @Past(message = "Дата рождения не может быть в будущем.")
    @Valid
    private LocalDate birthday;
}
