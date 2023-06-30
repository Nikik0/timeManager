package com.nikiko.timemanager.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.nikiko.timemanager.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EventRequestDto {
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private Long duration;
    private boolean enabled;
    private String shortDescription;
    private String fullDescription;
    //private LocalDateTime createdAt;
    //private LocalDateTime changedAt;
    private Long owner_id;
    private Long frequency;
}
