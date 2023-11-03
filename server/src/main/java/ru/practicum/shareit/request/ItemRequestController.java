package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST /requests");
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        log.info("Получен запрос GET /requests/all?from={}&size={}", from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}