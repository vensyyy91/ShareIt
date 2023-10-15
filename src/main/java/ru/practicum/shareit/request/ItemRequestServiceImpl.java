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
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
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
        checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userId);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest newRequest = itemRequestRepository.save(itemRequest);
        log.info(String.format("Пользователем с id=%d добавлен запрос: %s", userId, newRequest));

        return ItemRequestMapper.toItemRequestDto(newRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        checkUser(userId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequester(userId).stream()
                .map(this::mapItemRequestToDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
        log.info(String.format("Возвращен список всех запросов пользователя с id=%d: %s", userId, requests));

        return requests;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        checkUser(userId);
        List<ItemRequestDto> requests = itemRequestRepository.findAllByRequesterIsNot(
                        userId,
                        PageRequest.of(from / size, size, Sort.by("created").descending())
                ).get()
                .map(this::mapItemRequestToDto)
                .collect(Collectors.toList());
        log.info(String.format("Возвращен список всех запросов (from=%d, size=%d): %s", from, size, requests));

        return requests;
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        checkUser(userId);
        ItemRequestDto requestDto = mapItemRequestToDto(getItemRequest(requestId));
        log.info(String.format("Возвращен запрос с id=%d: %s", requestId, requestDto));

        return requestDto;
    }

    private void checkUser(long userId) {
        userRepository.findById(userId)
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