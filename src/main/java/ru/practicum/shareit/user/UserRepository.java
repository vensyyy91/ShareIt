package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findById(long userId);

    User save(User user);

    User update(User user);

    void delete(long userId);
}
