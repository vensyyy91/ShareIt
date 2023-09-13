package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public User toUser(UserDto userDto) {
        long id = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        return new User(id, name, email);
    }

    public User toUser(UserCreationDto userCreationDto) {
        String name = userCreationDto.getName();
        String email = userCreationDto.getEmail();
        return new User(0, name, email);
    }
}