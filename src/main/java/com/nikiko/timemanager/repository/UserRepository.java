package com.nikiko.timemanager.repository;

import com.nikiko.timemanager.entity.UserEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserEntity, Long> {
    @Modifying
    @Query("delete from users where id = $1")
    Mono<Void> deleteById(Long id);
}
