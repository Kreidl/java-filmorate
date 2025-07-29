package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение списка всех пользователей.");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на добавление нового пользователя.");
        userEmailValidation(user);
        userNameValidation(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно добавлен.");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        log.info("Запрос на обновление данных пользователя.");
        if (updatedUser.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (users.containsKey(updatedUser.getId())) {
            userEmailValidation(updatedUser);
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
        Optional<User> userOpt = users.values().stream()
                .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                .findFirst();
        if (userOpt.isPresent() && userOpt.get().getId() != user.getId()) {
            log.error("Пользователь ввёл Email, который уже используется.");
            throw new ValidationException("Такой Email уже используется.");
        }
    }

    private void userNameValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователь ввёл пустое имя, его именем установлен логин.");
        }
    }
}
