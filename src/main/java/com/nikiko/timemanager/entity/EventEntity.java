package com.nikiko.timemanager.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("Events")
public class EventEntity {
    @Id
    private Long id;
    private String name;
    @Column("start_time")
    private LocalDateTime startTime;
    private Long duration;
    private boolean enabled;
    @Column("short_description")
    private String shortDescription;
    @Column("full_description")
    private String fullDescription;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("changed_at")
    private LocalDateTime changedAt;
    @Column("owner_id")
    private Long ownerId;
    @Column("next_event_time")
    private LocalDateTime nextEventTime;
    @Column("was_postponed")
    private boolean postponed;
    @Column("last_happened")
    private LocalDateTime lastHappened;
    private Long frequency;
}
