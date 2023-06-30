package com.nikiko.timemanager.service;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.mapper.EventRequestMapper;
import com.nikiko.timemanager.mapper.EventResponseMapper;
import com.nikiko.timemanager.repository.EventRepository;
import com.nikiko.timemanager.repository.SubscriberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class EventService {
    private static final String BASE_URL = "http://localhost:8083/timemanager/api/v1";
    private final EventRepository eventRepository;

    private final EventRequestMapper requestMapper;

    @Value("${settings.delayEventTime}")
    private Long postponeMinutes;
    private final EventResponseMapper responseMapper;
    //todo needs to be deleted/webclient shit
    private final WebClient webClient;

    public EventService(EventRepository eventRepository, EventRequestMapper requestMapper, EventResponseMapper responseMapper) {
        this.eventRepository = eventRepository;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
        this.webClient = WebClient.builder().baseUrl(BASE_URL).build();
    }

    @Autowired
    private SubscriberRepository subscriberRepository;

    public Mono<String> call(){
        subscriberRepository.findActiveEventsForEachSubscriber().flatMap(s -> {
            log.info("--------------------\nentry from join");
            if (s == null) log.error("value was null");
            log.error(s.getClass().getName());
            log.info("value = " + s.toString() + "\n------------------");
            return Mono.just(s);
        }).subscribe();

        log.info("call initiated");
        /**/
        log.info("call was initiated");
        return this.webClient.get().uri(URL).retrieve().bodyToMono(String.class);
    }

    private final String URL = "/events/testcall";









    ///Todo refactor webclient calls
    public Mono<EventDto> getSingle(Long id){
        log.info("single " + id);
        return eventRepository.findById(id).map(responseMapper::mapEntityToResponse);
    }

    public Flux<EventDto> getAll(UserRequestDto owner){
        //log.info(owner.toString());
        return eventRepository.getEventEntitiesByOwnerId(owner.getId()).map(responseMapper::mapEntityToResponse);
    }

    public Mono<EventDto> change(EventRequestDto requestDto){
        return eventRepository.findById(requestDto.getId()).flatMap(event -> eventRepository.save(event.toBuilder()
                //todo разобраться с ошибками насчет нот нал + скорее всего придется писать свой парсер времени
                //.startTime(requestDto.getStartTime()) d
                //.duration(requestDto.getDuration())
                .enabled(requestDto.isEnabled())
                .name(requestDto.getName())
                .fullDescription(requestDto.getFullDescription())
                .shortDescription(requestDto.getShortDescription())
                .changedAt(LocalDateTime.now())
                .build())).map(responseMapper::mapEntityToResponse);
    }

    public Mono<EventDto> create(EventRequestDto requestDto){
        log.info(requestDto.toString());
        return eventRepository.save(requestMapper.mapFromRequestToEntity(requestDto)
                        .toBuilder()
                        .createdAt(LocalDateTime.now())
                        .changedAt(LocalDateTime.now())
                        .startTime(LocalDateTime.now())
                        .wasPostponed(false)
                        .nextEventTime(LocalDateTime.now().plusMinutes(requestDto.getFrequency()))
                        .lastHappened(LocalDateTime.now())
                        .build()
        ).map(responseMapper::mapEntityToResponse);
    }

    public Mono<EventDto> extend(EventRequestDto requestDto){
        return eventRepository.findById(requestDto.getId()).map(responseMapper::mapEntityToResponse);
    }

    public void delete(EventRequestDto requestDto){
        eventRepository.deleteById(requestDto.getId());
    }

    public Flux<EventEntity> getEventEntitiesByNextEventTime(LocalDateTime currentTime){
        return eventRepository.getEventEntitiesByNextEventTime(currentTime);
    }

    public Flux<EventEntity> getEventEntitiesByOwnerIdAndNextEventTimeAfter(Long userId, LocalDateTime time){
        return eventRepository.getEventEntitiesByOwnerIdAndNextEventTimeAfter(userId, time);
    }

    public void postponeEventsForUser(Long userId, Long additionalMinutes, LocalDateTime currentTime) {
        eventRepository.saveAll(
                this.getEventEntitiesByOwnerIdAndNextEventTimeAfter(userId, currentTime)
                        .flatMap(event -> {
                            event.setNextEventTime(
                                    event.getNextEventTime().plusMinutes(additionalMinutes)
                            );
                            return Mono.just(event);
                        })
        );
    }

    public void postponeEventAfterEvent(EventDto eventDto, LocalDateTime currentTime) {
        eventRepository.saveAll(
                this.getEventEntitiesByOwnerIdAndNextEventTimeAfter(eventDto.getOwner_id(), currentTime)
                        .flatMap(event -> {
                            if (event.getId() != eventDto.getId())
                                event.setNextEventTime(
                                        event.getNextEventTime().plusMinutes(postponeMinutes)
                                );
                            return Mono.just(event);
                        })
        );
    }

    /*
    | week 1 |                                                               | week 2 |
    | sunday | monday | tuesday | wednesday | Thursday | Friday | Saturday | | sunday | monday | tuesday | wednesday | Thursday | Friday | Saturday |
    fs        fst      fs

    f = first event, everyday in 12am
    s = second event, every monday in 14
    t = third event, everyday in 16

    each event lasts for 2 hours, so they almost overlap


    possible checks to make everything work properly
    1) postpone everything till the next time of this event ( check the time of the event and the next time of the postponed event )
    2) add column with boolean if the event was postponed. So the workflow of the event happening will change to:
    event happens -> send event notif -> check if the event was postponed (if it was then next time is next time - timeLag from postponing)
    -> write next event time (might need to have a check if the next event with lag is before the current minute)

    //todo just thought that we want to postpone the current event but it was already scheduled for the next date by the time user receives notif
    might help adding new column with last time event happened so
    if is the call to postpone was received we could fall back to last date and add to it postpone minutes
     */



    //this is testing of workflow explained above
    public void postponeEventBetween(EventRequestDto eventRequestDto, LocalDateTime currentTime) {
        log.info("postpone method started");
        eventRepository.findById(eventRequestDto.getId()).flatMap(eventEntity -> {
                    eventRepository.saveAll(
                            eventRepository.getEventEntitiesByOwnerIdAndNextEventTimeBetween(
                                    eventEntity.getOwnerId(), currentTime, eventEntity.getNextEventTime()
                                    )
                                    .flatMap(event -> {
                                        log.info("event before postpone " + event);
                                        event.setNextEventTime(
                                                eventEntity.getNextEventTime().plusMinutes(postponeMinutes)
                                        );
                                        log.info("event after postpone " + event);
                                        return Mono.just(event);
                                    })
                    );
                    return Mono.just(eventEntity);
                }
        );
        /*
        eventRepository.saveAll(
                eventRepository.getEventEntitiesByOwnerIdAndNextEventTimeBetween(event.getOwner_id(), currentTime,
                                eventDto.getNextEventTime())    //todo <- this is a possible problem if the event was changed while it was happening and client can't provide valid info
                        .flatMap(event -> {
                            event.setNextEventTime(
                                    eventDto.getNextEventTime().plusMinutes(additionalMinutes)
                            );
                            return Mono.just(event);
                        })
        );
        */
    }

}
