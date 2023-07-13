package com.nikiko.timemanager.service;


import com.nikiko.timemanager.dto.*;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.SubscriberEntity;
import com.nikiko.timemanager.exception.ApiException;
import com.nikiko.timemanager.mapper.EventResponseMapper;
import com.nikiko.timemanager.mapper.SubscriberMapper;
import com.nikiko.timemanager.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberNotificationService {
    private final SubscriberMapper subscriberMapper;
    private final SubscriberRepository subscriberRepository;
    private static final String BASE_URL = "http://localhost:8083/timemanager/api/v1";
    private final WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();
    private final EventService eventService;

    //todo this is a description of workflow for event time increase via notification
    /*
    event happens -> we want to postpone it -> click button for 30 extra minutes -> call about postponing this event comes ->
    |backend starts"| -> get all the events for this user with nextEventDate after the received event -> change nextEventDate to nextEventDate + 30 min and save to db ->
    -> send the notification that event was postponed successfully to all subs of this user
     */

    private final EventResponseMapper eventResponseMapper;
    private final Integer MAX_RETRIES_FOR_SUBSCRIBER = 100;
    private Map<Long, Map.Entry<List<EventDto>, Integer>> deliveryList = new HashMap<>();
    private void removeDeliveredEvents(SubscriberEntity sub, EventDto eventDto){
        log.info("delivered event " + eventDto + " was removed from delivery list");
        //todo check if performs or needs to be reassigned to older map
        Map.Entry<List<EventDto>, Integer> entry = deliveryList.get(sub.getSubscriberId());
        entry.getKey().remove(eventDto);
        if (entry.getKey().size() == 0)
            deliveryList.remove(sub);
    }

    private void retryIncrease(SubscriberEntity sub){
        if (!deliveryList.containsKey(sub)) return;
        if (Objects.equals(deliveryList.get(sub.getSubscriberId()).getValue(), MAX_RETRIES_FOR_SUBSCRIBER))
            deliveryList.remove(sub.getSubscriberId());
        else
            deliveryList.get(sub.getSubscriberId()).setValue(deliveryList.get(sub.getSubscriberId()).getValue() + 1);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void beginSendingNotificationsOnStartup(){
        LocalDateTime placeholderTime = LocalDateTime.of(2000,1,1,1,1);
        //todo placeholderTime should be changed to now()
        Runnable sendNotificationsRunnable = () -> startEventNotificationChain(placeholderTime);
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(sendNotificationsRunnable, 0 ,1, TimeUnit.MINUTES);
        log.info("placeholder time " + placeholderTime);
    }

    private void startEventNotificationChain(LocalDateTime currentTime){
        log.info("starting event sending at " + currentTime + " called by separate thread");
        List<Long> userIds = new ArrayList<>();
        List<EventEntity> events = new ArrayList<>();
        eventService.getEventEntitiesByNextEventTime(currentTime).flatMap(e -> {
            events.add(e);
            if (!userIds.contains(e.getOwnerId())){
                userIds.add(e.getOwnerId());
                subscriberRepository.getSubscriberEntitiesByUserId(e.getOwnerId()).flatMap(sub -> {
                    return Mono.just(Arrays.asList(
                            sub,
                            events.stream()
                                    .filter(eve -> Objects.equals(eve.getOwnerId(), sub.getUserId()))
                                    .collect(Collectors.toList())
                    ));                                     //todo <- this is disgusting and needs refactoring, should investigate usage of tuple
                }).subscribe(this::sendCurrentEventsToSubs);
            }
            return Mono.just(e);
        }).subscribe();
    }

    private void sendCurrentEventsToSubs(List<Object> objects){
        SubscriberEntity sub = (SubscriberEntity) objects.get(0);
        List<EventEntity> events = (List<EventEntity>) objects.get(1);
        log.info("sending events " + events + " to sub " + sub);
        deliveryList.put(sub.getSubscriberId(),
                new AbstractMap.SimpleEntry<>(
                    events.stream().map(eventResponseMapper::mapEntityToResponse).collect(Collectors.toList()),
                    0
        ));
        sendEvents(sub, events);
    }

    private void sendEvents(SubscriberEntity sub, List<EventEntity> events){
        for (EventEntity event: events) {
            log.info("now sending, event id is " + event.getId());
            retryIncrease(sub);
            updateEventAfterSending(event);
            sendEvent(sub,eventResponseMapper.mapEntityToResponse(event)).flatMap(health ->{
                log.info("sending was for sub " + sub + " mapped hc " + mapSubToFailedHC(sub));
                log.info(" health from sending " + health);
                if (health.isDeliverySuccessful())
                    removeDeliveredEvents(sub,eventResponseMapper.mapEntityToResponse(event));
                return Mono.just(health);
            }).subscribe();
        }
    }
    @Value("${settings.delayEventTime}")
    private Long postponeMinutes;
    private void updateEventAfterSending(EventEntity event){
        log.info("Update event invoked");
        //todo this could lead to bugs if the frequency is less than postpone time, testing required
        eventService.saveEntity(
                event.toBuilder()
                        .lastHappened(event.getNextEventTime())
                        .postponed(false)
                        .nextEventTime(event.isPostponed() ?
                                event.getNextEventTime().plusMinutes(event.getFrequency()).minusMinutes(postponeMinutes)
                                : event.getNextEventTime().plusMinutes(event.getFrequency()))
                        .build()
        ).subscribe();
        log.info("Updating event " + event.getId() + " nextTime is " + event.getNextEventTime() + " next event should be " +
                event.getNextEventTime().plusMinutes(event.getFrequency()).minusMinutes(postponeMinutes));
        log.info("Next event determined like this: " + event.getNextEventTime() + " + "
        + event.getFrequency() + " - " + postponeMinutes);
    }



    /*
    e1 10:30 freq 30
    e2 11:00 freq 60
    e3 11:30 freq 20
    e4 12:00
    e5 13:00

    was 11:00 should happen in 12:00
    postponed by 1.5h so next event in

     */

    private SubscriberHealthCheckDto mapSubToFailedHC(SubscriberEntity subscriberEntity){
        SubscriberHealthCheckDto hc = new SubscriberHealthCheckDto();
        return hc.toBuilder()
                .subscriberId(subscriberEntity.getSubscriberId())
                .enabled(subscriberEntity.isEnabled())
                .userId(subscriberEntity.getUserId())
                .endpoint(subscriberEntity.getEndpoint())
                .deliverySuccessful(false)
                .build();
    }


    //todo consider moving it to other class since endpoint calls may be used multiple times
    public Mono<SubscriberHealthCheckDto> sendEvent(SubscriberEntity sub, EventDto eventDto){
        log.info("Starting sending events to subs\n-----------------------------------");
        try {
            return webClient
                    .post()
                    .uri(sub.getEndpoint())
                    .bodyValue(eventDto)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.isError(), clientResponse -> Mono.error(new ApiException("Client endpoint not found","500")))
                    .bodyToMono(SubscriberHealthCheckDto.class)
                    .onErrorResume(e -> {
                        if (e instanceof UnknownHostException){
                            log.error("Unknown host " + sub.getEndpoint() + ", couldn't connect to provided host");
                        } else {
                            log.error("Error occurred while sending events to endpoint " + sub.getEndpoint());
                        }
                        return Mono.just(mapSubToFailedHC(sub));
                    });
        }catch (RuntimeException e){
            log.error("Error occurred " + e.getMessage() + ", error was caused by " + e.getCause());
            return Mono.just(mapSubToFailedHC(sub));
        }
    }

    public Mono<SubscriberResponseDto> subscribe(SubscriberRequestDto requestDto) {
      return subscriberRepository.findById(requestDto.getSubscriberId()).flatMap(subscriberEntity -> {
                    SubscriberEntity updatedEntity = subscriberEntity
                            .toBuilder()
                            .enabled(true)
                            .build();
                    subscriberRepository.save(updatedEntity).subscribe();
                    return Mono.just(updatedEntity);
                }
        ).map(subscriberMapper::mapEntityToResponse).flatMap(subscriberResponseDto -> {
            subscriberResponseDto.setSuccessful(true);
            return Mono.just(subscriberResponseDto);
        });
    }

    public Mono<SubscriberResponseDto> unsubscribe(SubscriberRequestDto requestDto) {
        return subscriberRepository.findById(requestDto.getSubscriberId()).flatMap(subscriberEntity ->
                subscriberRepository.save(subscriberEntity
                                        .toBuilder()
                                        .enabled(false)
                                        .build())
        ).map(subscriberMapper::mapEntityToResponse).flatMap(subscriberResponseDto -> {
            subscriberResponseDto.setSuccessful(true);
            return Mono.just(subscriberResponseDto);
        });
    }

    public Mono<SubscriberResponseDto> create(SubscriberRequestDto requestDto) {
        return subscriberRepository.save(subscriberMapper.mapRequestToEntity(requestDto))
                .map(subscriberMapper::mapEntityToResponse);
    }

    public Mono<Void> delete(SubscriberRequestDto requestDto) {
        return subscriberRepository.deleteById(requestDto.getSubscriberId());
    }

    public Flux<SubscriberResponseDto> getSubscribersInfo(SubscriberRequestDto requestDto) {
        return subscriberRepository.getSubscriberEntitiesByUserId(requestDto.getUserId()).map(subscriberMapper::mapEntityToResponse);
    }
}
