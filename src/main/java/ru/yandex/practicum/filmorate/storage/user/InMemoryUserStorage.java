package ru.yandex.practicum.filmorate.storage.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    @Getter
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        userEmailValidation(user);
        userNameValidation(user);
        user.setId(getNextId());
        System.out.println("Новый присвоенный id " + user.getId());
        users.put(user.getId(), user);
        log.info("Новый пользователь успешно добавлен.");
        return user;
    }

    @Override
    public User update(User updatedUser) {
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

    @Override
    public void delete(User user) {
        if (user.getId() < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id.");
        }
        if (users.containsKey(user.getId())) {
            users.remove(user);
            log.info("Пользователь успешно удалён.");
        }
        log.error("Пользователь ввёл несуществующий Id.");
        throw new NotFoundException("Такого пользователя не существует.");
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь ввёл несуществующий Id.");
            throw new NotFoundException("Пользователь с id=" + id + " не найден.");
        } else {
            return users.get(id);
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
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
