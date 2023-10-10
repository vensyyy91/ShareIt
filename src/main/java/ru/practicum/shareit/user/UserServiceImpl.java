package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Возвращен список пользователей: " + users);

        return users;
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = getUser(userId);
        log.info("Возвращен пользователь: " + user);

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Добавлен пользователь: " + newUser);

        return UserMapper.toUserDto(newUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = getUser(userId);
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        User updatedUser = userRepository.save(user);
        log.info("Обновлен пользователь: " + updatedUser);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        getUser(userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь с id=" + userId);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден."));
    }
}