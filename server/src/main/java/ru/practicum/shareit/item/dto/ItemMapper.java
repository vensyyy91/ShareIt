package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        Long id = item.getId();
        String name = item.getName();
        String description = item.getDescription();
        Boolean isAvailable = item.getAvailable();
        Long requestId = item.getRequest();

        return new ItemDto(id, name, description, isAvailable, requestId);
    }

    public ItemInfoDto toItemInfoDto(Item item,
                                     BookingInfoDto lastBooking,
                                     BookingInfoDto nextBooking,
                                     List<CommentDto> comments) {
        return ItemInfoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking((nextBooking))
                .comments(comments)
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequestId())
                .build();
    }
}