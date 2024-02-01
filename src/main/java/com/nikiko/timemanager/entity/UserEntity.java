package com.nikiko.timemanager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("Users")
public class UserEntity {
    @Id
    private Long Id;
    private String name;
    private String password;
    private boolean blocked;
    @Column("role")
    private UserRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ToString.Include(name = "password")
    private String mask(){ return "*****";}
}
