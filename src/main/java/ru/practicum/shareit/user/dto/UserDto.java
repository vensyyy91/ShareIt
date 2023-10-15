package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.ValidationOnCreate;
import ru.practicum.shareit.validation.ValidationOnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    @NotBlank(groups = ValidationOnCreate.class)
    private String name;
    @Email(groups = {ValidationOnCreate.class, ValidationOnUpdate.class})
    @NotBlank(groups = ValidationOnCreate.class)
    private String email;
}