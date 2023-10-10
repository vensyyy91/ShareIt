package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос POST /requests");
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info(String.format("Получен запрос GET /requests/all?from=%d&size=%d", from, size));
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/" + requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}