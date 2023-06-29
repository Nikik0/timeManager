package com.nikiko.timemanager.repository;

import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.SubscriberEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.cdi.Eager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SubscriberRepository extends R2dbcRepository<SubscriberEntity, Long> {
    @Query("DELETE from subscribers where id = $1")
    Mono<Void> deleteById(Long id);

    //@Query("SELECT subscribers s from subscribers left join events on subscribers.\"userId\" = Events.owner_id where subscribers.id = 2")
    @Query("SELECT s, e from subscribers s, events e where s.id = 2")

//    @Query("SELECT s from subscribers s where s.id = 2")
//    @Query("select * from subscribers left join events on subscribers.user_id = events.owner_id")
//    Flux<SubscriberEntity> findActiveEventsForEachSubscriber();
    Flux<Map.Entry<Object, Object>> findActiveEventsForEachSubscriber();
//    Flux<Map.Entry<SubscriberEntity, EventEntity>> findActiveEventsForEachSubscriber();
    //    Flux<String> findActiveEventsForEachSubscriber();
    Flux<SubscriberEntity> getSubscriberEntitiesByUserId(Long userId);
}
