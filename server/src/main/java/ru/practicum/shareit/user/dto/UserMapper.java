package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public User toUser(UserDto userDto) {
        Long id = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        return new User(id, name, email);
    }
}