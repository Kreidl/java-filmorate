package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void createUsers() {
        user1 = User.builder()
                .email("example1@ex.ru")
                .login("Пользователь1")
                .birthday(LocalDate.of(2000,1,1))
                .name("Пользователь1")
                .build();
        user2 = User.builder()
                .email("example2@ex.ru")
                .login("Пользователь2")
                .birthday(LocalDate.of(2000,2,2))
                .name("Пользователь2")
                .build();
        user3 = User.builder()
                .email("example3@ex.ru")
                .login("Пользователь3")
                .birthday(LocalDate.of(2000,3,3))
                .name("Пользователь3")
                .build();
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя")
    @DirtiesContext
    void createUserTest() {
        Optional<User> userOpt = Optional.of(userStorage.createUser(user1));
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "example1@ex.ru")
                                .hasFieldOrPropertyWithValue("login", "Пользователь1")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000,1,1))
                                .hasFieldOrPropertyWithValue("name", "Пользователь1")
                );
    }

    @Test
    @DisplayName("Проверка добавления нового пользователя с id")
    @DirtiesContext
    void createUserWithIdTest() {
        userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2.setId(1L);
        user2 = userStorage.createUser(user2);
        assertEquals(1, user1.getId());
        assertNotEquals(user1.getId(), user2.getId());
    }

    @Test
    @DisplayName("Проверка обновления пользователя")
    @DirtiesContext
    void updateUserTest() {
        userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        User updatedUser = user1.builder()
                .id(user1.getId())
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("ОбновлённыйПользователь1")
                .build();
        Optional<User> userOpt = Optional.of(userStorage.updateUser(updatedUser));
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).isEqualTo(updatedUser)
                );
    }

    @Test
    @DisplayName("Проверка обновления пользователя с несуществующим id")
    @DirtiesContext
    void updateUserWithIncorrectIdTest() {
        User updatedUser = user1.builder()
                .id(1L)
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("ОбновлённыйПользователь1")
                .build();
        assertThatThrownBy(() -> userStorage.updateUser(updatedUser))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");
    }

    @Test
    @DisplayName("Проверка обновления пользователя без id")
    @DirtiesContext
    void updateUserWithoutIdTest() {
        userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        User updatedUser = user1.builder()
                .email("example@ex.ru")
                .login("ОбновлённыйПользователь1")
                .birthday(LocalDate.of(2000,10,10))
                .name("ОбновлённыйПользователь1")
                .build();
        assertThatThrownBy(() -> userStorage.updateUser(updatedUser))
                .isInstanceOf(InternalServerException.class)
                .hasMessage("Не удалось обновить данные");
    }

    @Test
    @DisplayName("Проверка удаления пользователя")
    @DirtiesContext
    void deleteUserTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        userStorage.deleteUser(user1.getId());
        assertEquals(0, userStorage.getAllUsers().size(), "Пользователь не удалён");
    }

    @Test
    @DisplayName("Проверка получения всех пользователей")
    @DirtiesContext
    void getAllUsersTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2 = userStorage.createUser(user2);
        assertEquals(2, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user3 = userStorage.createUser(user3);
        assertEquals(3, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        List<User> expUsers = new ArrayList<>();
        expUsers.add(user1);
        expUsers.add(user2);
        expUsers.add(user3);
        List<User> users = userStorage.getAllUsers();
        assertThat(users).isEqualTo(expUsers);
    }

    @Test
    @DisplayName("Проверка получения пользователя")
    @DirtiesContext
    void getUserByIdTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        Optional<User> userOpt = userStorage.getUserById(user1.getId());
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).isEqualTo(user1)
                );
    }

    @Test
    @DisplayName("Проверка получения пользователя с несуществующим id")
    @DirtiesContext
    void getUserByIncorrectIdTest() {
        user1.setId(1L);
        assertThatThrownBy(() -> userStorage.getUserById(user1.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    @DisplayName("Проверка наличия пользователя в БД")
    @DirtiesContext
    void isContainsTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        assertEquals(true, userStorage.isContains(user1.getId()), "Пользователя нет в БД");
        assertThatThrownBy(() -> userStorage.isContains(2L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    @DisplayName("Проверка добавления пользователя в друзья")
    @DirtiesContext
    void addFriendTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2 = userStorage.createUser(user2);
        assertEquals(2, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> expFriends = new ArrayList<>();
        expFriends.add(user2);
        assertThat(userStorage.getFriends(user1.getId())).isEqualTo(expFriends);
    }

    @Test
    @DisplayName("Проверка удаления пользователя из друзей")
    @DirtiesContext
    void deleteFriendTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2 = userStorage.createUser(user2);
        assertEquals(2, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        userStorage.addFriend(user1.getId(), user2.getId());
        assertThat(userStorage.getFriends(user1.getId()).getFirst()).isEqualTo(user2);
        userStorage.deleteFriend(user1.getId(), user2.getId());
        assertThat(userStorage.getFriends(user1.getId())).isEqualTo(new ArrayList<User>());
    }

    @Test
    @DisplayName("Проверка получения всех друзей пользователя")
    @DirtiesContext
    void getFriendsTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2 = userStorage.createUser(user2);
        assertEquals(2, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user3 = userStorage.createUser(user3);
        assertEquals(3, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        List<User> expFriends = new ArrayList<>();
        expFriends.add(user2);
        expFriends.add(user3);
        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.addFriend(user1.getId(), user3.getId());
        List<User> friends = userStorage.getFriends(user1.getId());
        assertThat(friends).isEqualTo(expFriends);

    }

    @Test
    @DisplayName("Проверка получения общих друзей двух пользователей")
    @DirtiesContext
    void getCommonFriendsTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user2 = userStorage.createUser(user2);
        assertEquals(2, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        user3 = userStorage.createUser(user3);
        assertEquals(3, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        List<User> expComFriends = new ArrayList<>();
        expComFriends.add(user3);
        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());
        List<User> comFriends = userStorage.getFriends(user1.getId());
        assertThat(comFriends).isEqualTo(expComFriends);
    }

    @Test
    @DisplayName("Проверка наличия пользователя с определённым email")
    @DirtiesContext
    void isEmailIsUsedTest() {
        user1 = userStorage.createUser(user1);
        assertEquals(1, userStorage.getAllUsers().size(), "Пользователь не добавлен");
        assertTrue(userStorage.isEmailIsUsed(user1.getEmail()));
        assertFalse(userStorage.isEmailIsUsed("noemail@list.ru"));
    }
}