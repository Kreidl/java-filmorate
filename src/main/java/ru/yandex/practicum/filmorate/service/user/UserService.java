package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.mapper.UserMapper.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(NewUserRequest request) {
        log.debug("Начало добавления пользователя {}", request);
        User user = mapToUser(request);
        if (userStorage.isEmailIsUsed(request.getEmail())) {
            log.error("Email {} уже используется.", request.getEmail());
            throw  new ValidationException("Email " + request.getEmail() + " уже используется.");
        }
        user = userStorage.createUser(user);
        log.debug("Пользователь {} добавлен.", user);
        return mapToUserDto(user);
    }

    public UserDto updateUser(UpdateUserRequest request) {
        log.debug("Начало обновления пользователя {}", request);
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> updateUserFields(user, request))
                .orElseThrow(() -> {
                    log.error("Пользователь с id = {} не найден.", request.getId());
                    return new NotFoundException("Пользователь с id = " + request.getId() + " не найден.");
                });
        updatedUser = userStorage.updateUser(updatedUser);
        log.info("Пользователь {} обновлён.", updatedUser);
        return mapToUserDto(updatedUser);
    }

    public UserDto getUserById(Long id) {
        log.debug("Получаем пользователя с id {}.", id);
        return userStorage.getUserById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> {
                    log.error("Пользователь с id = {} не найден.", id);
                    return new NotFoundException("Пользователь с id = " + id + " не найден.");
                });
    }

    public Collection<UserDto> getAllUsers() {
        log.debug("Получаем список всех пользователей.");
        return userStorage.getAllUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public void addFriend(Long userId, Long newFriendId) {
        log.debug("Добавление пользователем с id {} пользователя с id {} в друзья.", userId, newFriendId);
        isUserInDb(userId);
        isUserInDb(newFriendId);
        userStorage.addFriend(userId, newFriendId);
        log.info("Друг {} успешно добавлен пользователю {}.", newFriendId, userId);
}

    public void deleteFriend(Long userId, Long friendId) {
        log.debug("Удаление пользователем с id {} пользователя с id {} из друзей.", userId, friendId);
        isUserInDb(userId);
        isUserInDb(friendId);
        userStorage.deleteFriend(userId,friendId);
        log.info("У пользователя с id={} друг с id={} успешно удалён.", userId, friendId);
    }

    public Collection<UserDto> getAllFriends(Long userId) {
        log.debug("Получаем список друзей у пользователя с id {}.", userId);
        isUserInDb(userId);
        return userStorage.getFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long userId, Long otherUserId) {
        log.debug("Получаем список общих друзей у пользователей с id {} и {}.", userId, otherUserId);
        isUserInDb(userId);
        isUserInDb(otherUserId);
        return userStorage.getCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private void isUserInDb(Long id) {
        log.debug("Проверка на наличие в БД пользователя с id {}.", id);
        if (!userStorage.isContains(id)) {
            log.error("Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }
}
