package com.nikiko.timemanager.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberHealthCheckDto {
    private Long subscriberId;
    private boolean enabled;
    private boolean deliverySuccessful;
    private String endpoint;
    private Long userId;
}
