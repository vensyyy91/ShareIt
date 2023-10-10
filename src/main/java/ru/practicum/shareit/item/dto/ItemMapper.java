package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.Item;

import java.util.List;

public class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getAvailable();
        long requestId = item.getRequest();

        return new ItemDto(id, name, description, isAvailable, requestId);
    }

    public static ItemInfoDto toItemInfoDto(Item item,
                                            BookingInfoDto lastBooking,
                                            BookingInfoDto nextBooking,
                                            List<CommentDto> comments) {
        long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getAvailable();

        return new ItemInfoDto(id, name, description, isAvailable, lastBooking, nextBooking, comments);
    }

    public static Item toItem(ItemDto itemDto) {
        long id = itemDto.getId();
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();
        long request = itemDto.getRequestId();

        return new Item(id, name, description, null, isAvailable, request);
    }
}