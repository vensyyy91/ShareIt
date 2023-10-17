package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void testUserDto_withAllFields_shouldHaveAllJsonFields() {
        UserDto userDto = new UserDto(1L, "user1", "user1@test.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@test.com");

    }

    @Test
    @SneakyThrows
    void testUserDto_withOnlyName_shouldHaveOnlyNameJsonField() {
        UserDto userDto = new UserDto(null, "user1", null);

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user1");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.email");

    }

    @Test
    @SneakyThrows
    void testUserDto_withOnlyEmail_shouldHaveOnlyEmailJsonField() {
        UserDto userDto = new UserDto(null, null, "user1@test.com");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).hasJsonPathStringValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user1@test.com");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.name");
    }
}