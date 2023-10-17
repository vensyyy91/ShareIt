package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testItemDto_withAllFields_shouldHaveAllJsonFields() {
        ItemDto itemDto = new ItemDto(1L, "item1",  "test item description", true, 1L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    @SneakyThrows
    void testItemDto_withOnlyName_shouldHaveOnlyNameJsonField() {
        ItemDto itemDto = new ItemDto(null, "item1",  null, null, null);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.description");
        assertThat(result).doesNotHaveJsonPathValue("$.available");
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
    }

    @Test
    @SneakyThrows
    void testItemDto_withOnlyDescription_shouldHaveOnlyDescriptionJsonField() {
        ItemDto itemDto = new ItemDto(null, null,  "test item description", null, null);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test item description");
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.name");
        assertThat(result).doesNotHaveJsonPathValue("$.available");
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
    }

    @Test
    @SneakyThrows
    void testItemDto_withOnlyAvailable_shouldHaveOnlyAvailableJsonField() {
        ItemDto itemDto = new ItemDto(null, null,  null, true, null);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).doesNotHaveJsonPathValue("$.id");
        assertThat(result).doesNotHaveJsonPathValue("$.name");
        assertThat(result).doesNotHaveJsonPathValue("$.description");
        assertThat(result).doesNotHaveJsonPathValue("$.requestId");
    }
}