package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.*;

@Repository
public class UserInMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new TreeMap<>();
    private long id;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(long userId) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден.");
        }
        return users.get(userId);
    }

    @Override
    public User save(User user) {
        if (users.values().stream().anyMatch(usr -> usr.getEmail().equals(user.getEmail()))) {
            throw new EmailAlreadyExistException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        String oldName = oldUser.getName();
        String oldEmail = oldUser.getEmail();
        if (user.getEmail() == null) {
            user.setEmail(oldEmail);
        } else if (users.values().stream()
                .anyMatch(usr -> !usr.getEmail().equals(oldEmail) && usr.getEmail().equals(user.getEmail()))) {
            throw new EmailAlreadyExistException("Пользователь с email " + user.getEmail() + " уже существует.");
        }
        if (user.getName() == null) {
            user.setName(oldName);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    private long generateId() {
        return ++id;
    }
}
