package com.nikiko.timemanager.repository;

import com.nikiko.timemanager.entity.EventEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface EventRepository extends R2dbcRepository<EventEntity, Long> {
    //Flux<EventEntity> getEventEntitiesByOwner_id(Long id);
    //@Modifying
    @Query("DELETE from events where id = $1")
    Mono<Void> deleteById(Long id);
    //@Query("SELECT * from Events where id = 2")
    //Mono<EventEntity> selectWithId(Long id);


    //todo untested
    Flux<EventEntity> getEventEntitiesByNextEventTime(LocalDateTime time);
    Flux<EventEntity> getEventEntitiesByNextEventTimeBetween(LocalDateTime time, LocalDateTime nextTime);

    //@Modifying
    //@Query("update events set name = 'changed' where id = 4")
    //Mono<EventEntity> testChange();
    Flux<EventEntity> getEventEntitiesByOwnerId(Long id);

    Flux<EventEntity> getEventEntitiesByOwnerIdAndNextEventTimeAfter(Long id, LocalDateTime currentTime);

    Flux<EventEntity> getEventEntitiesByOwnerIdAndNextEventTimeBetween(Long id, LocalDateTime currentTime, LocalDateTime nextTime);

}






