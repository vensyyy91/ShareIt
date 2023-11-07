package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationOnCreate;
import ru.practicum.shareit.validation.ValidationOnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    @NotBlank(groups = ValidationOnCreate.class)
    private String name;
    @Email(groups = {ValidationOnCreate.class, ValidationOnUpdate.class})
    @NotBlank(groups = ValidationOnCreate.class)
    private String email;
}