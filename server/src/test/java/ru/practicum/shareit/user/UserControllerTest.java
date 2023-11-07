package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;
    private static final String USERS_PATH = "/users";

    @Test
    @SneakyThrows
    void getAllUsers_shouldReturnOk() {
        mvc.perform(get(USERS_PATH)).andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    void getUserById_shouldReturnOk() {
        mvc.perform(get(USERS_PATH + "/1")).andExpect(status().isOk());

        verify(userService).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void createUser_shouldReturnOkAndUser() {
        UserDto user = new UserDto(1L, "user1", "user1@test.com");
        when(userService.createUser(user)).thenReturn(user);

        String result = mvc.perform(post(USERS_PATH).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    @SneakyThrows
    void updateUser_shouldReturnOk() {
        UserDto userForUpdate = new UserDto(1L, "updatedUser", "update@test.com");
        when(userService.updateUser(1L, userForUpdate)).thenReturn(userForUpdate);

        String result = mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userForUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateUser_withNoEmail_shouldReturnOkAndUpdatedUser() {
        UserDto userForUpdate = new UserDto(null, "updatedUser", null);
        UserDto userAfterUpdate = new UserDto(1L, "updatedUser", "user1@test.com");
        when(userService.updateUser(1L, userForUpdate)).thenReturn(userAfterUpdate);

        String result = mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateUser_withNoName_shouldReturnOkAndUpdatedUser() {
        UserDto userForUpdate = new UserDto(null, null, "updated@test.com");
        UserDto userAfterUpdate = new UserDto(1L, "user1", "updated@test.com");
        when(userService.updateUser(1L, userForUpdate)).thenReturn(userAfterUpdate);

        String result = mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void deleteUser_shouldReturnOk() {
        mvc.perform(delete(USERS_PATH + "/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(anyLong());
    }
}