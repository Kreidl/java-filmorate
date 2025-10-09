package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exceptions.ErrorHandler;
import ru.yandex.practicum.filmorate.service.user.UserService;

import ru.yandex.practicum.filmorate.dto.UserDto;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Import(ErrorHandler.class)
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        log.info("Запрос на получение списка всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id={}.", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable Long id) {
        log.info("Запрос на получение друзей пользователя с id={}.", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на получение общих друзей пользователей с id={} и id={}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Запрос на добавление нового пользователя {}.", request);
        return userService.createUser(request);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Запрос на обновление данных пользователя {}.", request);
        return userService.updateUser(request);
    }

    @PutMapping("/{id}/friends/{newFriendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long newFriendId) {
        log.info("Запрос пользователя с id={} на добавление в друзья пользователя с id={}.", id, newFriendId);
        userService.addFriend(id, newFriendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос пользователя с id={} на удаление из друзей пользователя с id={}.", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
