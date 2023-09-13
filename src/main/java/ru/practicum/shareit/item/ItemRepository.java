package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    List<Item> findAll(long userId);

    Item findById(long itemId);

    Item save(Item item);

    Item update(Item item);

    List<Item> search(String text);
}