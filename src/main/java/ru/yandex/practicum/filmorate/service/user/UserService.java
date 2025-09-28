package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.create(user);
    }

    public User update(User updatedUser) {
        return userStorage.update(updatedUser);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(long userId, long newFriendId) {
        System.out.println("Список пользователей " + getAllUsers());
        validateId(userId);
        validateId(newFriendId);
        if (!getUserById(userId).getFriends().contains(newFriendId) || getUserById(userId).getFriends().isEmpty()) {
            getUserById(userId).getFriends().add(newFriendId);
            getUserById(newFriendId).getFriends().add(userId);
            log.info("У пользователя с id={} друг с id={} успешно добавлен.", userId, newFriendId);
        } else {
            log.error("У пользователя с id={} друг с id={} уже добавлен.", userId, newFriendId);
            throw new DuplicateException("Друг с id=" + newFriendId + " уже добавлен.");
        }
    }

    public void deleteFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        if (getAllUsers().contains(getUserById(userId))) {
            if (getAllUsers().contains(getUserById(friendId))) {
                getUserById(userId).getFriends().remove(friendId);
                getUserById(friendId).getFriends().remove(userId);
                log.info("У пользователя с id={} друг с id={} успешно удалён.", userId, friendId);
            }
        } else {
            log.info("У пользователя с id={} друг с id={} не найден.", userId, friendId);
            throw new NotFoundException("Пользователь с id=" + friendId + " у пользователя с id=" + userId + " не найден.");
        }
    }

    public Collection<User> getAllFriends(long userId) {
        validateId(userId);
        return getUserById(userId).getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(long userId, long otherUserId) {
        validateId(userId);
        validateId(otherUserId);
        Set<Long> userFriends = getUserById(userId).getFriends();
        Set<Long> friendFriends = getUserById(otherUserId).getFriends();
        if (userFriends != null && friendFriends != null) {
            boolean hasIntersection = userFriends.stream().anyMatch(friendFriends::contains);
            if (hasIntersection) {
                Set<Long> commonFriends = new HashSet<>(userFriends);
                commonFriends.retainAll(friendFriends);
                return commonFriends.stream()
                        .map(userStorage::getUserById)
                        .toList();
            } else {
                log.error("У пользователей с id = {} и id = {} нет общих друзей", userId, otherUserId);
                throw new NotFoundException("У пользователей с id=" + userId + " и id=" + otherUserId + " нет общих друзей");
            }
        } else {
            log.error("У пользователей с id = {} и id = {} нет общих друзей", userId, otherUserId);
            throw new NotFoundException("У пользователей с id=" + userId + " и id=" + otherUserId + " нет общих друзей");
        }
    }

    private void validateId(long userId) {
        if (userId < 1) {
            log.error("Пользователь ввёл некорректный Id.");
            throw new ValidationException("Указан некорректный Id пользователя.");
        }
        if (!getAllUsers().contains(getUserById(userId))) {
            log.error("Пользователь ввёл несуществующий Id.");
            throw new NotFoundException("Указан несуществующий Id пользователя.");
        }
    }
}
