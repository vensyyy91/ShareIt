package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationOnCreate;
import ru.practicum.shareit.validation.ValidationOnUpdate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Получен запрос GET /users/" + userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@Validated(ValidationOnCreate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @Validated(ValidationOnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users/" + userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/" + userId);
        userService.deleteUser(userId);
    }
}