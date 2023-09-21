package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Item;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getAvailable();

        return new ItemDto(id, name, description, isAvailable);
    }

    public static Item toItem(ItemDto itemDto) {
        long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();

        return new Item(id, name, description, 0, isAvailable, 0);
    }
}