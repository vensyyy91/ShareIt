package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
        User user = checkUser(userId);
        log.info("Возвращен пользователь: " + user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Добавлен пользователь: " + newUser);

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User oldUser = checkUser(userId);
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
    public void deleteUser(long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь с id=" + userId);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id= " + userId + " не найден."));
    }
}