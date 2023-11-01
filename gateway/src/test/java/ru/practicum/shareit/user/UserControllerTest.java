package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private UserClient userClient;
    private static final String USERS_PATH = "/users";

    @Test
    @SneakyThrows
    void getAllUsers_shouldReturnOk() {
        mvc.perform(get(USERS_PATH)).andExpect(status().isOk());

        verify(userClient).getUsers();
    }

    @Test
    @SneakyThrows
    void getUserById_shouldReturnOk() {
        mvc.perform(get(USERS_PATH + "/1")).andExpect(status().isOk());

        verify(userClient).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void createUser_shouldReturnOkAndUser() {
        UserDto user = new UserDto("user1", "user1@test.com");
        ResponseEntity<Object> response = new ResponseEntity<>(user, HttpStatus.OK);
        when(userClient.createUser(user)).thenReturn(response);

        String result = mvc.perform(post(USERS_PATH).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @Test
    @SneakyThrows
    void createUser_withInvalidEmail_shouldReturnBadRequest() {
        UserDto user = new UserDto("user1", "user1.com");

        mvc.perform(post(USERS_PATH).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserDto.class));
    }

    @Test
    @SneakyThrows
    void createUser_withNoEmail_shouldReturnBadRequest() {
        UserDto user = new UserDto("user1", null);

        mvc.perform(post(USERS_PATH).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).createUser(any(UserDto.class));
    }

    @Test
    @SneakyThrows
    void updateUser_shouldReturnOk() {
        UserDto userForUpdate = new UserDto("updatedUser", "update@test.com");
        ResponseEntity<Object> response = new ResponseEntity<>(userForUpdate, HttpStatus.OK);
        when(userClient.updateUser(1L, userForUpdate)).thenReturn(response);

        String result = mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userForUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateUser_withInvalidEmail_shouldReturnBadRequest() {
        UserDto userForUpdate = new UserDto("updatedUser", "update.com");

        mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(anyLong(), any(UserDto.class));
    }

    @Test
    @SneakyThrows
    void updateUser_withNoEmail_shouldReturnOkAndUpdatedUser() {
        UserDto userForUpdate = new UserDto("updatedUser", null);
        UserDto userAfterUpdate = new UserDto("updatedUser", "user1@test.com");
        ResponseEntity<Object> response = new ResponseEntity<>(userAfterUpdate, HttpStatus.OK);
        when(userClient.updateUser(1L, userForUpdate)).thenReturn(response);

        String result = mvc.perform(patch(USERS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateUser_withNoName_shouldReturnOkAndUpdatedUser() {
        UserDto userForUpdate = new UserDto(null, "updated@test.com");
        UserDto userAfterUpdate = new UserDto("user1", "updated@test.com");
        ResponseEntity<Object> response = new ResponseEntity<>(userAfterUpdate, HttpStatus.OK);
        when(userClient.updateUser(1L, userForUpdate)).thenReturn(response);

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

        verify(userClient).deleteUser(anyLong());
    }
}