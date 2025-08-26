package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User create(User user);

    public User update(User updatedUser);

    public void delete(User user);

    public Collection<User> getAllUsers();

    public User getUserById(long id);
}
