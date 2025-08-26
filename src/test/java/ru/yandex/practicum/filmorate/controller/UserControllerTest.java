package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя с корректными данными")
    void createUserTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .build();
        user.setName("Пользователь");
        userController.create(user);
        assertNotNull(userController.findAll(), "Список пользователей пустой");
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя с Email, который уже используется")
    void createUserWithDuplicateEmailTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .build();
        userController.create(user);
        User user1 = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(1997,10,10))
                .name("Пользователь")
                .build();
        assertThrows(ValidationException.class, () -> {
            userController.create(user1);
        });
        assertEquals(1, userController.findAll().size(), "Пользователь с дублирующимся Email не должен быть добавлен в список пользователей");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с пустым именем")
    void createUserWithEmptyNameTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .build();
        userController.create(user);
        assertNotNull(userController.findAll(), "Список пользователей пустой");
        assertEquals(user.getName(), user.getLogin(), "Пустой name должен быть заменён на login");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с некорректным Email")
    void validateUserWithIncorrectEmailTest() {
        User user = User.builder()
                .email("example.ex@ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в Email проигнорирована");
        assertEquals("Пользователь ввёл некорректный Email.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка валидации пользователя с некорректным логином")
    void validateUserWithIncorrectLoginTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .build();
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
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2026,10,10))
                .name("Пользователь")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Ошибка в дате рождения проигнорирована");
        assertEquals("Дата рождения не может быть в будущем.", violations.stream().findFirst().get().getMessage(),
                "Некорректная ошибка");
    }

    @Test
    @DisplayName("Проверка обновления пользователя с корректными данными")
        void updateUserTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .build();
        userController.create(user);
        User updatedUser = User.builder()
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .id(user.getId())
                .build();
        userController.update(updatedUser);
        assertEquals(updatedUser.getLogin(), user.getLogin(), "Login пользователя не обновлён");
    }

    @Test
    @DisplayName("Проверка обновления имени пользователя на пустое")
    void updateUserWithEmptyNameTest() {
        User user = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .build();
        userController.create(user);
        User updatedUser = User.builder()
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .build();
        updatedUser.setId(user.getId());
        userController.update(updatedUser);
        assertEquals(updatedUser.getLogin(), user.getName(), "Имя пользователя не обновлено");
    }

    @Test
    @DisplayName("Проверка обновления несуществующего пользователя")
    void updateUserWithNonExistentIdTest() {
        User updatedUser = User.builder()
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь")
                .id(3L)
                .build();
        assertThrows(NotFoundException.class, () -> {
            userController.update(updatedUser);
        });
        assertEquals(0, userController.findAll().size(), "Пользователь не должен быть добавлен в список пользователей");
    }

    @Test
    @DisplayName("Проверка корректного возвращения всех пользователей")
    void findAllTest() {
        User user1 = User.builder()
                .email("example@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("Пользователь1")
                .build();
        User user2 = User.builder()
                .email("example2@ex.ru")
                .login("Пользователь2")
                .birthday(LocalDate.of(2002,10,10))
                .name("Пользователь2")
                .build();
        userController.create(user1);
        userController.create(user2);
        HashMap<Integer, User> users = new HashMap<>();
        users.put(1, user1);
        users.put(2, user2);
        assertEquals(users.values().toString(), userController.findAll().toString(), "Список пользователей некорректный");
    }
}