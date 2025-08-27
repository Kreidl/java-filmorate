package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ErrorHandler;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Import(ErrorHandler.class)
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение списка всех пользователей.");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Запрос на получение пользователя с id={}.", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("Запрос на получение друзей пользователя с id={}.", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Запрос на получение общих друзей пользователей с id={} и id={}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Запрос на добавление нового пользователя {}.", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User updatedUser) {
        log.info("Запрос на обновление данных пользователя {}.", updatedUser);
        return userService.update(updatedUser);
    }

    @PutMapping("/{id}/friends/{newFriendId}")
    public void addFriend(@PathVariable long id, @PathVariable long newFriendId) {
        log.info("Запрос пользователя с id={} на добавление в друзья пользователя с id={}.", id, newFriendId);
        userService.addFriend(id, newFriendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Запрос пользователя с id={} на удаление из друзей пользователя с id={}.", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}
