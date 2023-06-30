package com.nikiko.timemanager.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "subscribers")
@Builder(toBuilder = true)
public class SubscriberEntity {
    private Long id;
    private String endpoint;
    private Long userId;
    private boolean enabled;
}
