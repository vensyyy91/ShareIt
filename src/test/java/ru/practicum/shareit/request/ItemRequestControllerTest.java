package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

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
    private ItemRequestService itemRequestService;
    private static final String REQUESTS_PATH = "/requests";

    @Test
    @SneakyThrows
    void addRequest_withUserIdAndValidDto_shouldReturnOk() {
        ItemRequestDto itemRequest = new ItemRequestDto(
                1L,
                "test request",
                1L,
                LocalDateTime.now(),
                null
        );
        when(itemRequestService.addRequest(1, itemRequest)).thenReturn(itemRequest);

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
        ItemRequestDto itemRequest = new ItemRequestDto(
                1L,
                "test request",
                1L,
                LocalDateTime.now(),
                null
        );

        mvc.perform(post(REQUESTS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void addRequest_withoutDescription_shouldReturnBadRequest() {
        ItemRequestDto itemRequest = new ItemRequestDto(
                1L,
                null,
                1L,
                LocalDateTime.now(),
                null
        );

        mvc.perform(post(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    @SneakyThrows
    void getRequests_withUserId_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestService).getUserRequests(1L);
    }

    @Test
    @SneakyThrows
    void getRequests_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH)).andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getUserRequests(anyLong());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withUserIdAndWithParams_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllRequests(1, 1, 5);
    }

    @Test
    @SneakyThrows
    void getAllRequests_withUserIdAndWithoutParams_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllRequests(1, 0, 10);
    }

    @Test
    @SneakyThrows
    void getAllRequests_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")).andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withInvalidFromParam_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllRequests_withInvalidSizeParam_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getRequestById_withUserId_shouldReturnOk() {
        mvc.perform(get(REQUESTS_PATH + "/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemRequestService).getRequestById(1, 1);
    }

    @Test
    @SneakyThrows
    void getRequestById_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(REQUESTS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(itemRequestService, never()).getRequestById(anyLong(), anyLong());
    }
}