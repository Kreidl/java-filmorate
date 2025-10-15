package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User mapToUser(NewUserRequest request) {
        log.debug("Начало конвертации запроса в объект класса User.");
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setBirthday(request.getBirthday());
        log.debug("Окончание конвертации запроса в объект класса User.");
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        log.debug("Начало конвертации объекта User в объект класса UserDto.");
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setName(user.getName());
        dto.setBirthday(user.getBirthday());
        log.debug("Окончание конвертации объекта User в объект класса UserDto.");
        return dto;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        log.debug("Начало обновления полей объекта User из запроса.");
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }
        log.debug("Окончание обновления полей объекта Film из запроса.");
        return user;
    }
}
