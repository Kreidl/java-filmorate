package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение списка всех пользователей.");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Запрос на добавление нового пользователя.");
        userEmailValidation(user);
        userLoginValidation(user.getLogin());
        userBirthdayValidation(user.getBirthday());
        userNameValidation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно добавлен.");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User updatedUser) {
        log.info("Запрос на обновление данных пользователя.");
        if (updatedUser.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (users.containsKey(updatedUser.getId())) {
            userEmailValidation(updatedUser);
            userLoginValidation(updatedUser.getLogin());
            userBirthdayValidation(updatedUser.getBirthday());
            userNameValidation(updatedUser);
            User oldUser = users.get(updatedUser.getId());
            oldUser.setName(updatedUser.getName());
            oldUser.setBirthday(updatedUser.getBirthday());
            oldUser.setLogin(updatedUser.getLogin());
            oldUser.setEmail(updatedUser.getEmail());
            log.info("Данные пользователя успешно обновлены.");
            return oldUser;
        }
        log.error("Пользователь ввёл несуществующий Id.");
        throw new NotFoundException("Такого пользователя не существует.");
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void userEmailValidation(User user) {
        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            log.error("Пользователь ввёл пустой Email.");
            throw new ValidationException("Email не может быть пустым.");
        }
        final String EMAIL_REGEX =
                "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!Pattern.matches(EMAIL_REGEX, user.getEmail())) {
            log.error("Пользователь ввёл некорректный Email.");
            throw new ValidationException("Указан некорректный Email.");
        }
        Optional<User> userOpt = users.values().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst();
        if (userOpt.isPresent() && userOpt.get().getId() != user.getId()) {
            log.error("Пользователь ввёл Email, который уже используется.");
            throw new ValidationException("Такой Email уже используется.");
        }
    }

    private void userLoginValidation(String login) {
        if (login.isEmpty() || login.isBlank()) {
            log.error("Пользователь ввёл пустой логин.");
            throw new ValidationException("Логин не может быть пустым.");
        }
        if (login.contains(" ")) {
            log.error("Пользователь ввёл логин, содержащий пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }

    private void userNameValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователь ввёл пустое имя, его именем установлен логин.");
        }
    }

    private void userBirthdayValidation(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            log.error("Пользователь ввёл дату рождения из будущего.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
