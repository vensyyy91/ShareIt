package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {
    List<ItemInfoDto> getAllItems(long userId);

    ItemInfoDto getItemById(long userId, long itemId);

    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}