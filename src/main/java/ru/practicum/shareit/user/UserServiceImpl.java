package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Возвращен список пользователей: " + users);

        return users;
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId);
        log.info("Возвращен пользователь: " + user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserCreationDto userCreationDtoDto) {
        User user = userMapper.toUser(userCreationDtoDto);
        User newUser = userRepository.save(user);
        log.info("Добавлен пользователь: " + newUser);

        return userMapper.toUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setId(userId);
        User updatedUser = userRepository.update(user);
        log.info("Обновлен пользователь: " + updatedUser);

        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.delete(userId);
        log.info("Удален пользователь с id=" + userId);
    }
}
