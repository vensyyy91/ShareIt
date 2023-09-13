package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getAvailable();

        return new ItemDto(id, name, description, isAvailable);
    }

    public Item toItem(ItemDto itemDto) {
        long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();

        return new Item(id, name, description, 0, isAvailable, 0);
    }

    public Item toItem(ItemCreationDto itemCreationDto) {
        String name = itemCreationDto.getName();
        String description = itemCreationDto.getDescription();
        Boolean isAvailable = itemCreationDto.getAvailable();

        return new Item(0, name, description, 0, isAvailable, 0);
    }
}
