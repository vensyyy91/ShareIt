package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    private static final String ITEMS_PATH = "/items";

    @Test
    @SneakyThrows
    void getAllItems_withUserIdAndWithParams_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemService).getAllItems(1, 1, 5);
    }

    @Test
    @SneakyThrows
    void getAllItems_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH)).andExpect(status().isBadRequest());

        verify(itemService, never()).getAllItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemById_withUserId_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH + "/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService).getItemById(1, 1);
    }

    @Test
    @SneakyThrows
    void getItemById_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(itemService, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void addItem_withUserIdAndValidDto_shouldReturnOk() {
        ItemDto item = new ItemDto(1L, "item1", "first test item description", true, null);
        when(itemService.addItem(1, item)).thenReturn(item);

        String result = mvc.perform(post(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(item), result);
    }

    @Test
    @SneakyThrows
    void addItem_withoutUserId_shouldReturnBadRequest() {
        ItemDto item = new ItemDto(1L, "item1", "first test item description", true, null);

        mvc.perform(post(ITEMS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_withUserIdAndValidDto_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto(1L, "updatedItem", "updated description", false, null);
        when(itemService.updateItem(1, 1, itemForUpdate)).thenReturn(itemForUpdate);

        String result = mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemForUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateItem_withoutUserId_shouldReturnBadRequest() {
        ItemDto itemForUpdate = new ItemDto(1L, "updatedItem", "updated description", false, null);

        mvc.perform(patch(ITEMS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_withOnlyName_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto(null, "updatedItem", null, null, null);
        ItemDto itemAfterUpdate = new ItemDto(
                1L,
                "updatedItem",
                "first test item description",
                true,
                null);
        when(itemService.updateItem(1, 1, itemForUpdate)).thenReturn(itemAfterUpdate);

        String result = mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateItem_withOnlyDescription_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto(null, null, "updated description", null, null);
        ItemDto itemAfterUpdate = new ItemDto(
                1L,
                "item1",
                "updated description",
                true,
                null);
        when(itemService.updateItem(1, 1, itemForUpdate)).thenReturn(itemAfterUpdate);

        String result = mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateItem_withOnlyAvailable_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto(null, null, null, false, null);
        ItemDto itemAfterUpdate = new ItemDto(
                1L,
                "item1",
                "first test item description",
                false,
                null);
        when(itemService.updateItem(1, 1, itemForUpdate)).thenReturn(itemAfterUpdate);

        String result = mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void searchItem_withValidParams_shouldReturnOk() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", "first item");
        parameters.add("from", "0");
        parameters.add("size", "5");

        mvc.perform(get(ITEMS_PATH + "/search").params(parameters)).andExpect(status().isOk());

        verify(itemService).searchItem("first item", 0, 5);
    }

    @Test
    @SneakyThrows
    void searchItem_withEmptyText_shouldReturnOk() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", "");
        parameters.add("from", "0");
        parameters.add("size", "5");

        mvc.perform(get(ITEMS_PATH + "/search").params(parameters)).andExpect(status().isOk());

        verify(itemService).searchItem("", 0, 5);
    }

    @Test
    @SneakyThrows
    void addComment_withUserIdAndValidDto_shouldReturnOk() {
        CommentDto comment = new CommentDto(1L, "test comment", "user1", LocalDateTime.now());
        when(itemService.addComment(1, 1, comment)).thenReturn(comment);

        String result = mvc.perform(post(ITEMS_PATH + "/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(comment), result);
    }

    @Test
    @SneakyThrows
    void addComment_withoutUserId_shouldReturnBadRequest() {
        CommentDto comment = new CommentDto(1L, "test comment", "user1", LocalDateTime.now());

        mvc.perform(post(ITEMS_PATH + "/1/comment")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andReturn().getResponse().getContentAsString();

        verify(itemService, never()).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}