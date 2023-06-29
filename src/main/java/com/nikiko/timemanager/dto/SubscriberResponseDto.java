package com.nikiko.timemanager.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

//Todo could use refactoring, this is pretty much a placeholder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubscriberResponseDto {
    private Long subscriberId;
    private boolean successful;
}
