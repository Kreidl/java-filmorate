package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

UserController userController;

@BeforeEach
void createNewUserController() {
    userController = new UserController();
}

@Test
void createUserTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    userController.create(user);
    assertNotNull(userController.getUsers(), "Список пользователей пустой");
}

@Test
void createUserWithIncorrectEmailTest() {
    User user = new User("example.ex@ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.create(user);
    });
    assertEquals(new HashMap<>(), userController.getUsers(), "Пользователь не должен быть добавлен в список пользователей");
}

@Test
void createUserWithDuplicateEmailTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    userController.create(user);
    User user1 = new User("example@ex.ru", "Пользователь1", LocalDate.of(1997,10,10));
    assertThrows(ValidationException.class, () -> {
        User user2 = userController.create(user1);
    });
    assertEquals(1, userController.getUsers().size(), "Пользователь с дублирующимся Email не должен быть добавлен в список пользователей");
}

@Test
void createUserWithIncorrectLoginTest() {
    User user = new User("example@ex.ru", "", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.create(user);
    });
    assertEquals(new HashMap<>(), userController.getUsers(), "Пользователь не должен быть добавлен в список пользователей");
    user.setLogin("П о льзователь");
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.create(user);
    });
    assertEquals(new HashMap<>(), userController.getUsers(), "Пользователь не должен быть добавлен в список пользователей");
}

@Test
void createUserWithEmptyNameTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    userController.create(user);
    assertNotNull(userController.getUsers(), "Список пользователей пустой");
    assertEquals(user.getName(), user.getLogin(), "Пустой name должен быть заменён на login");
}

@Test
void createUserWithIncorrectBirthdayTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2026,10,10));
    user.setName("Пользователь");
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.create(user);
    });
    assertEquals(new HashMap<>(), userController.getUsers(), "Пользователь не должен быть добавлен в список пользователей");
}

@Test
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
void updateUserWithIncorrectEmailTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    userController.create(user);
    User updatedUser = new User("exa@mp.l-_?eexru", "ОбновлённыйПользователь1", LocalDate.of(2000,10,10));
    updatedUser.setName(user.getName());
    updatedUser.setId(user.getId());
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.update(updatedUser);
    });
    assertNotEquals(updatedUser.getEmail(), user.getEmail(), "Email пользователя обновлён");
}

@Test
void updateUserWithIncorrectLoginTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    userController.create(user);
    User updatedUser = new User("example@ex.ru", "", LocalDate.of(2000,10,10));
    updatedUser.setName(user.getName());
    updatedUser.setId(user.getId());
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.update(updatedUser);
    });
    assertNotEquals(updatedUser.getLogin(), user.getLogin(), "Login пользователя обновлён");
    updatedUser.setLogin("П о льзователь1");
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.update(updatedUser);
    });
    assertNotEquals(updatedUser.getLogin(), user.getLogin(), "Login пользователя обновлён");
}

@Test
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
void updateUserWithIncorrectBirthdayTest() {
    User user = new User("example@ex.ru", "Пользователь1", LocalDate.of(2000,10,10));
    user.setName("Пользователь");
    userController.create(user);
    User updatedUser = new User("example@ex.ru", "ОбновлённыйПользователь1", LocalDate.of(2026,10,10));
    updatedUser.setName(user.getName());
    updatedUser.setId(user.getId());
    assertThrows(ValidationException.class, () -> {
        User user1 = userController.update(updatedUser);
    });
    assertNotEquals(updatedUser.getBirthday(), user.getBirthday(), "Дата рождения пользователя обновлена");
}

@Test
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