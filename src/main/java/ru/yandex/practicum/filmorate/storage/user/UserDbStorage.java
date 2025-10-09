package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository("userDbStorage")
@Slf4j
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users ORDER BY user_id ASC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_FRIENDSHIP_QUERY = "INSERT INTO friendships (user_id, friend_id) " +
            "VALUES(?, ?);";
    private static final String DELETE_FRIENDSHIP_QUERY = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT u.* FROM friendships f LEFT JOIN users u " +
            "ON f.friend_id = u.user_id WHERE f.user_id = ?";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT u.* FROM (SELECT * FROM friendships " +
            "WHERE user_id = ?) f1 JOIN (SELECT * FROM friendships WHERE user_id = ?) f2 " +
            "ON f1.friend_id = f2.friend_id JOIN users u ON f1.friend_id = u.user_id";

    public UserDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new UserRowMapper());
    }

    @Override
    public User createUser(User user) {
        log.debug("Создание нового пользователя {}.", user);
        long id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        user.setId(id);
        log.trace("Пользователь {} создан", user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        log.debug("Обновление пользователя с id {}.", updatedUser.getId());
        update(UPDATE_QUERY,
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getName(),
                updatedUser.getBirthday(),
                updatedUser.getId());
        log.trace("Пользователь с id {} обновлён.", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Удаление пользователя с id {}.", id);
        delete(DELETE_QUERY, id);
        log.trace("Пользователь с id {} удалён.", id);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Получение всех пользователей.");
        List<User> users = findMany(FIND_ALL_QUERY);
        log.trace("Список пользователей {}", users);
        return users;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.debug("Получение пользователя с id {}.", id);
        Optional<User> user = findOne(FIND_BY_ID_QUERY, id);
        log.trace("Пользователь {} получен.", user.get());
        return user;
    }

    @Override
    public boolean isContains(Long id) {
        return getUserById(id).isPresent();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.debug("Добавление в друзья пользователем с id {} пользователя с id {}.", userId, friendId);
        jdbc.update(INSERT_FRIENDSHIP_QUERY,
                userId,
                friendId
        );
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.debug("Удаление из друзей пользователем с id = {} пользователя с id = {}.", userId, friendId);
        jdbc.update(DELETE_FRIENDSHIP_QUERY,
                userId,
                friendId
        );
    }

    public List<User> getFriends(Long id) {
        log.debug("Получение друзей пользователя с id {}.", id);
        return findMany(FIND_FRIENDS_QUERY, id);
    }

    public List<User> getCommonFriends(Long userId, Long userId2) {
        log.debug("Получение общих друзей пользователей с id {} и {}.", userId, userId2);
        List<User> commonFriends = findMany(FIND_COMMON_FRIENDS_QUERY, userId, userId2);
        if (commonFriends.isEmpty()) {
            return Collections.emptyList();
        }
        return findMany(FIND_COMMON_FRIENDS_QUERY, userId, userId2);
    }

    public boolean isEmailIsUsed(String email) {
        Optional<User> userOpt = findOne(FIND_BY_EMAIL_QUERY, email);
        return userOpt.isPresent();
    }
}
