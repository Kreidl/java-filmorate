package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class NewUserRequest {
    @NotBlank(message = "Email пользователя не может быть пустым.")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Пользователь ввёл некорректный Email.")
    @Valid
    private String email;

    @NotBlank(message = "Логин пользователя не может содержать пробелы или быть пустым.")
    @Pattern(regexp = "\\S+", message = "Логин пользователя не может содержать пробелы или быть пустым.")
    @Valid
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    @Valid
    private LocalDate birthday;
}
