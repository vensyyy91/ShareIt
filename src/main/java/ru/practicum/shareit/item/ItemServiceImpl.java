package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getAllItems(long userId) {
        List<ItemDto> items = itemRepository.findAll(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info(String.format("Возвращен список вещей для пользователя с id=%d: %s", userId, items));

        return items;
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.findById(itemId);
        log.info("Возвращена вещь: " + item);

        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto addItem(long userId, ItemCreationDto itemCreationDto) {
        userRepository.findById(userId);
        Item item = itemMapper.toItem(itemCreationDto);
        item.setOwner(userId);
        Item newItem = itemRepository.save(item);
        log.info(String.format("Пользователем с id=%d добавлена вещь: %s", userId, newItem));

        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        long ownerId = itemRepository.findById(itemId).getOwner();
        if (userId != ownerId) {
            throw new AccessDeniedException("Редактировать вещь может только её владелец.");
        }
        Item item = itemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(userId);
        Item updatedItem = itemRepository.update(item);
        log.info("Владельцем обновлена вещь: " + updatedItem);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> foundItems = itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info(String.format("Выполнен поиск по запросу: \"%s\". Найдены вещи: %s", text, foundItems));

        return foundItems;
    }
}
