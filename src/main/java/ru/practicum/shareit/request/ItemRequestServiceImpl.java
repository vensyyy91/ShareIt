package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        ItemRequest newRequest = itemRequestRepository.save(itemRequest);
        log.info("Пользователем с id={} добавлен запрос: {}", userId, newRequest);

        return ItemRequestMapper.toItemRequestDto(newRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        getUser(userId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(this::mapItemRequestToDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
        log.info("Возвращен список всех запросов пользователя с id={}: {}", userId, requests);

        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        getUser(userId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequesterIdIsNot(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
                ).get()
                .map(this::mapItemRequestToDto)
                .collect(Collectors.toList());
        log.info("Возвращен список всех запросов (from={}, size={}): {}", from, size, requests);

        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        getUser(userId);
        ItemRequestDto requestDto = mapItemRequestToDto(getItemRequest(requestId));
        log.info("Возвращен запрос с id={}: {}", requestId, requestDto);

        return requestDto;
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + userId + " не найден."));
    }

    private ItemRequest getItemRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Запрос с id=" + requestId + " не найден."));
    }

    private ItemRequestDto mapItemRequestToDto(ItemRequest itemRequest) {
        List<ItemDto> items = itemRepository.findAllByRequest(itemRequest.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}