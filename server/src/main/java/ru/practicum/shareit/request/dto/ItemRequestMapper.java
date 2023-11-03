package ru.practicum.shareit.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        Long id = itemRequest.getId();
        String description = itemRequest.getDescription();
        Long requester = itemRequest.getRequester().getId();
        LocalDateTime created = itemRequest.getCreated();

        return new ItemRequestDto(id, description, requester, created, new ArrayList<>());
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        Long id = itemRequest.getId();
        String description = itemRequest.getDescription();
        Long requester = itemRequest.getRequester().getId();
        LocalDateTime created = itemRequest.getCreated();

        return new ItemRequestDto(id, description, requester, created, items);
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());

        return itemRequest;
    }
}