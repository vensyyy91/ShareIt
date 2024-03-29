package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestClient itemRequestClient;
    private static final String REQUESTS_PATH = "/requests";

    @Test
    @SneakyThrows
    void addRequest_withUserIdAndValidDto_shouldReturnOk() {
        ItemRequestDto itemRequest = new ItemRequestDto("test request");
        ResponseEntity<Object> response = new ResponseEntity<>(itemRequest, HttpStatus.OK);
        when(itemRequestClient.addRequest(1, itemRequest)).thenReturn(response);

        String result = mvc.perform(post(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }

    @Test
    @SneakyThrows
    void addRequest_withoutUserId_shouldReturnBadRequest() {
        ItemRequestDto itemRequest = new ItemRequestDto("test request");

        mvc.perform(post(REQUESTS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void addRequest_withoutDescription_shouldReturnBadRequest() {
        ItemRequestDto itemRequest = new ItemRequestDto(null);

        mvc.perform(post(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void addRequest_withLongDescription_shouldReturnBadRequest() {
        String description = "Testing is a crucial part of the software development process. " +
                "It helps to ensure that the code is error-free and functions as intended. " +
                "There are different types of testing, including unit testing, functional testing, regression testing, " +
                "and performance testing. Each type of test focuses on different aspects of the software, " +
                "such as the correctness of the code, the user experience, or the speed of the application. " +
                "By conducting these tests, the developers can identify and fix any problems in the software " +
                "before it is released to the users. This helps to improve the quality of the product " +
                "and increases customer satisfaction.";
        ItemRequestDto itemRequest = new ItemRequestDto(description);

        mvc.perform(post(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void getRequests_withUserId_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequests(1);
    }

    @Test
    @SneakyThrows
    void getRequests_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH)).andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequests(anyLong());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withUserIdAndWithParams_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(1, 1, 5);
    }

    @Test
    @SneakyThrows
    void getAllRequests_withUserIdAndWithoutParams_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(1, 0, 10);
    }

    @Test
    @SneakyThrows
    void getAllRequests_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")).andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withInvalidFromParam_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withInvalidSizeParam_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getRequestById_withUserId_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(1, 1);
    }

    @Test
    @SneakyThrows
    void getRequestById_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(anyLong(), anyLong());
    }
}