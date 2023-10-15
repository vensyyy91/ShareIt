package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationOnCreate;
import ru.practicum.shareit.validation.ValidationOnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private Long id;
    @NotBlank(groups = ValidationOnCreate.class)
    private String name;
    @NotBlank(groups = ValidationOnCreate.class)
    @Size(max = 200, groups = {ValidationOnCreate.class, ValidationOnUpdate.class})
    private String description;
    @NotNull(groups = ValidationOnCreate.class)
    private Boolean available;
    private Long requestId;
}