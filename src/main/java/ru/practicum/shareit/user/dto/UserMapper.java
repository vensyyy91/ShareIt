package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toUserDto(User user) {
        long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();

        return new UserDto(id, name, email);
    }

    public static User toUser(UserDto userDto) {
        long id = userDto.getId();
        String name = userDto.getName();
        String email = userDto.getEmail();

        return new User(id, name, email);
    }
}