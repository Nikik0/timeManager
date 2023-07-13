package com.nikiko.timemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nikiko.timemanager.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequestDto{
    private Long id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
