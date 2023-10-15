package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testItemRequestDto_withAllFields_shouldHaveAllJsonFields() {
        ItemDto itemDto = new ItemDto(1L, "item1", "test item", true, 1L);
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "test request",
                1L,
                LocalDateTime.now(),
                Collections.singletonList(itemDto)
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test request");
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.created").isNotNull();
        assertThat(result).extractingJsonPathArrayValue("$.items").isNotEmpty();
    }

    @Test
    @SneakyThrows
    void testItemRequestDto_withOnlyDescription_shouldHaveOnlyDescriptionJsonField() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null,
                "test request",
                null,
                null,
                null
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test request");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.requester");
        assertThat(result).doesNotHaveJsonPathValue("$.created");
        assertThat(result).doesNotHaveJsonPathValue("$.items");
    }
}