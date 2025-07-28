package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserControllerTest {

    UserController userController;

    private static Validator validator;

    @BeforeEach
    void createNewUserController() {
        userController = new UserController();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя с корректными данными")
    void createUserTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        userController.create(user);
        assertNotNull(userController.getUsers(), "Список пользователей пустой");
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя с Email, который уже используется")
    void createUserWithDuplicateEmailTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        userController.create(user);
        User user1 = new User("example@ex.ru", "Пользователь1", LocalDate.of(1997,10,10));
        assertThrows(ValidationException.class, () -> {
            userController.create(user1);
        });
        assertEquals(1, userController.getUsers().size(), "Пользователь с дублирующимся Email не должен быть добавлен в список пользователей");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с пустым именем")
    void createUserWithEmptyNameTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        userController.create(user);
        assertNotNull(userController.getUsers(), "Список пользователей пустой");
        assertEquals(user.getName(), user.getLogin(), "Пустой name должен быть заменён на login");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с некорректным Email")
    void validateUserWithIncorrectEmailTest() {
        User user = new User("example.ex@ru", "Пользователь1", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в Email проигнорирована");
        assertEquals("Пользователь ввёл некорректный Email.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с некорректным логином")
    void validateUserWithIncorrectLoginTest() {
        User user = new User("example@ex.ru", "", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в логине проигнорирована");
        assertEquals("Логин пользователя не может содержать пробелы или быть пустым.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
        user.setLogin("П о льзователь");
        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в логине проигнорирована");
        assertEquals("Логин пользователя не может содержать пробелы или быть пустым.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с некорректной датой рождения")
    void validateUserWithIncorrectBirthdayTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2026,10,10));
        user.setName("Пользователь");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в дате рождения проигнорирована");
        assertEquals("Дата рождения не может быть в будущем.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка обновления пользователя с корректными данными")
        void updateUserTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        userController.create(user);
        User updatedUser = new User("example@ex.ru", "ОбновлённыйПользователь1", LocalDate.of(2000,10,10));
        updatedUser.setName(user.getName());
        updatedUser.setId(user.getId());
        userController.update(updatedUser);
        assertEquals(updatedUser.getLogin(), user.getLogin(), "Login пользователя не обновлён");
    }

    @Test
    @DisplayName("Проверка обновления имени пользователя на пустое")
    void updateUserWithEmptyNameTest() {
        User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        user.setName("Пользователь");
        userController.create(user);
        User updatedUser = new User("example@ex.ru", "ОбновлённыйПользователь1", LocalDate.of(2000,10,10));
        updatedUser.setId(user.getId());
        userController.update(updatedUser);
        assertEquals(updatedUser.getLogin(), user.getName(), "Имя пользователя не обновлено");
    }

    @Test
    @DisplayName("Проверка корректного возвращения всех пользователей")
    void findAllTest() {
        User user1 = new User("example1@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
        User user2 = new User("example2@ex.ru", "Пользователь2", LocalDate.of(2002,10,10));
        userController.create(user1);
        userController.create(user2);
        HashMap<Integer, User> users = new HashMap<>();
        users.put(1, user1);
        users.put(2, user2);
        assertEquals(users.values().toString(), userController.findAll().toString(), "Список пользователей некорректный");
    }
}