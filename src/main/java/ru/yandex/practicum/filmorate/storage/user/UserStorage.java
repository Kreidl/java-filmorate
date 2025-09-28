package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User updatedUser);

    void delete(User user);

    Collection<User> getAllUsers();

    User getUserById(long id);
}
