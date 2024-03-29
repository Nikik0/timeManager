package com.nikiko.timemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "subscribers")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SubscriberEntity {
    @Id
    @Column("id")
    private Long subscriberId;
    private String endpoint;
    @Column("user_id")
    private Long userId;
    private boolean enabled;
}
