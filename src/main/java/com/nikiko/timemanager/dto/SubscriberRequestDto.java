package com.nikiko.timemanager.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubscriberRequestDto {
    @Id
    private Long subscriberId;
    private boolean enabled;
    private Long userId;
    private String endpoint;
}
