package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User updatedUser);

    void deleteUser(Long id);

    Collection<User> getAllUsers();

    Optional<User> getUserById(Long id);

    boolean isContains(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    public List<User> getCommonFriends(Long userId, Long userId2);

    public boolean isEmailIsUsed(String email);
}
