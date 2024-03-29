package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}