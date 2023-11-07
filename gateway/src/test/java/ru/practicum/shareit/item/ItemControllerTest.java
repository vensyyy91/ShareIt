package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

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
    private ItemClient itemClient;
    private static final String ITEMS_PATH = "/items";

    @Test
    @SneakyThrows
    void getAllItems_withUserIdAndWithParams_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(itemClient).getItems(1, 1, 5);
    }

    @Test
    @SneakyThrows
    void getAllItems_withUserIdAndWithoutParams_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH).header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getItems(1, 0, 10);
    }

    @Test
    @SneakyThrows
    void getAllItems_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH)).andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllItems_withInvalidFromParam_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getAllItems_withInvalidSizeParam_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getItemById_withUserId_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH + "/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemClient).getItemById(1, 1);
    }

    @Test
    @SneakyThrows
    void getItemById_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(itemClient, never()).getItemById(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void addItem_withUserIdAndValidDto_shouldReturnOk() {
        ItemDto item = new ItemDto("item1", "first test item description", true, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(item, headers, HttpStatus.OK);
        when(itemClient.addItem(1, item)).thenReturn(response);

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
        ItemDto item = new ItemDto("item1", "first test item description", true, null);

        mvc.perform(post(ITEMS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void addItem_withEmptyName_shouldReturnBadRequest() {
        ItemDto item = new ItemDto(null, "first test item description", true, null);

        mvc.perform(post(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void addItem_withEmptyDescription_shouldReturnBadRequest() {
        ItemDto item = new ItemDto("item1", null, true, null);

        mvc.perform(post(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void addItem_withLongDescription_shouldReturnBadRequest() {
        String description = "Testing is a crucial part of the software development process. " +
                "It helps to ensure that the code is error-free and functions as intended. " +
                "There are different types of testing, including unit testing, functional testing, regression testing, " +
                "and performance testing. Each type of test focuses on different aspects of the software, " +
                "such as the correctness of the code, the user experience, or the speed of the application. " +
                "By conducting these tests, the developers can identify and fix any problems in the software " +
                "before it is released to the users. This helps to improve the quality of the product " +
                "and increases customer satisfaction.";
        ItemDto item = new ItemDto("item1", description, true, null);

        mvc.perform(post(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void addItem_withoutAvailable_shouldReturnBadRequest() {
        ItemDto item = new ItemDto("item1", "first test item description", null, null);

        mvc.perform(post(ITEMS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_withUserIdAndValidDto_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto("updatedItem", "updated description", false, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(itemForUpdate, headers, HttpStatus.OK);
        when(itemClient.updateItem(1, 1, itemForUpdate)).thenReturn(response);

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
        ItemDto itemForUpdate = new ItemDto("updatedItem", "updated description", false, null);

        mvc.perform(patch(ITEMS_PATH + "/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_withOnlyName_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto("updatedItem", null, null, null);
        ItemDto itemAfterUpdate = new ItemDto(
                "updatedItem",
                "first test item description",
                true,
                null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(itemAfterUpdate, headers, HttpStatus.OK);
        when(itemClient.updateItem(1, 1, itemForUpdate)).thenReturn(response);

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
        ItemDto itemForUpdate = new ItemDto(null, "updated description", null, null);
        ItemDto itemAfterUpdate = new ItemDto(
                "item1",
                "updated description",
                true,
                null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(itemAfterUpdate, headers, HttpStatus.OK);
        when(itemClient.updateItem(1, 1, itemForUpdate)).thenReturn(response);

        String result = mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemAfterUpdate), result);
    }

    @Test
    @SneakyThrows
    void updateItem_withLongDescription_shouldReturnBadRequest() {
        String description = "Testing is a crucial part of the software development process. " +
                "It helps to ensure that the code is error-free and functions as intended. " +
                "There are different types of testing, including unit testing, functional testing, regression testing, " +
                "and performance testing. Each type of test focuses on different aspects of the software, " +
                "such as the correctness of the code, the user experience, or the speed of the application. " +
                "By conducting these tests, the developers can identify and fix any problems in the software " +
                "before it is released to the users. This helps to improve the quality of the product " +
                "and increases customer satisfaction.";
        ItemDto itemForUpdate = new ItemDto(null, description, null, null);

        mvc.perform(patch(ITEMS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemForUpdate)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).updateItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    @SneakyThrows
    void updateItem_withOnlyAvailable_shouldReturnOk() {
        ItemDto itemForUpdate = new ItemDto(null, null, false, null);
        ItemDto itemAfterUpdate = new ItemDto(
                "item1",
                "first test item description",
                false,
                null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(itemAfterUpdate, headers, HttpStatus.OK);
        when(itemClient.updateItem(1, 1, itemForUpdate)).thenReturn(response);

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

        mvc.perform(get(ITEMS_PATH + "/search")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isOk());

        verify(itemClient).search(1, "first item", 0, 5);
    }

    @Test
    @SneakyThrows
    void searchItem_withEmptyText_shouldReturnOk() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", "");
        parameters.add("from", "0");
        parameters.add("size", "5");

        mvc.perform(get(ITEMS_PATH + "/search")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isOk());

        verify(itemClient).search(1, "", 0, 5);
    }

    @Test
    @SneakyThrows
    void searchItem_withInvalidFromParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", "first item");
        parameters.add("from", "-1");
        parameters.add("size", "5");

        mvc.perform(get(ITEMS_PATH + "/search").params(parameters)).andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItem_withInvalidSizeParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("text", "first item");
        parameters.add("from", "0");
        parameters.add("size", "0");

        mvc.perform(get(ITEMS_PATH + "/search").params(parameters)).andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void searchItem_withoutFromAndSizeParams_shouldReturnOk() {
        mvc.perform(get(ITEMS_PATH + "/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "first item"))
                .andExpect(status().isOk());

        verify(itemClient).search(1, "first item", 0, 10);
    }

    @Test
    @SneakyThrows
    void searchItem_withoutTextParam_shouldReturnBadRequest() {
        mvc.perform(get(ITEMS_PATH + "/search")).andExpect(status().isBadRequest());

        verify(itemClient, never()).search(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void addComment_withUserIdAndValidDto_shouldReturnOk() {
        CommentDto comment = new CommentDto("test comment");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(comment, headers, HttpStatus.OK);
        when(itemClient.addComment(1, 1, comment)).thenReturn(response);

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
        CommentDto comment = new CommentDto("test comment");


        mvc.perform(post(ITEMS_PATH + "/1/comment")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andReturn().getResponse().getContentAsString();

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }

    @Test
    @SneakyThrows
    void addComment_withEmptyText_shouldReturnBadRequest() {
        CommentDto comment = new CommentDto("");

        mvc.perform(post(ITEMS_PATH + "/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andReturn().getResponse().getContentAsString();

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(CommentDto.class));
    }
}