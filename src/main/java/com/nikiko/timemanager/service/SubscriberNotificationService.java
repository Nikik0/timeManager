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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberNotificationService {
    private final SubscriberMapper subscriberMapper;
    private final SubscriberRepository subscriberRepository;
    private final UserService userService;

    //todo placeholder methods for health checking subscribers
    private static final String BASE_URL = "http://localhost:8083/timemanager/api/v1";
    private final String URL = "/events/testcall";
    private final WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();

    public Mono<SubscriberHealthCheckDto> checkHC(SubscriberEntity subscriberEntity){
        //todo check response time, if more than 30 sec or response is bad or succ status isn't good then put the input dto to some kind of list and check always
        return this.webClient.post().uri(URL).bodyValue(subscriberMapper.mapEntityToResponse(subscriberEntity)).retrieve().bodyToMono(SubscriberHealthCheckDto.class);//.subscribe(s -> log.error(s.toString()));
    }


    //map for undelivered if undeliv then add to map< key is SubDto and value is retries

    //или через деку, тогда два метода, один раз в минуту берет все записи из бд и добавляет их в деку
    //второй берет каждую запись из деки и проверяет ее хс, если все ок то убирает ее из деки, если не ок то оставляет?

    //не понимаю зачем брать всех
    //скорее всего выбрать всех юзеров у которых есть евент в текущую минуту, если он есть то отправляем уведомление на всех сабов этого юзера, если не получилось то добавляем саба в деку
    //возможно для такого надо сделать новую дто

    //получить ивенты в минуту - вытащить с них айди юзера - по айди найти всеъ сабов его - отправить сабу дто с ивентами нужными
    //наверное надо посмотреть какие есть возможные варианты получить только уникальные значения через бд чтоб собрать каждого юзера только один раз


    //если неуспешно то скорее всего надо сохранять айди саба в ключ + лист с евентами в значение,
    // при сохранении проверять есть ли такой саб, если есть то расширять лист а не заменять
    //возможно надо добавить еще одно значение на количество ретраев, если ретраев больше трешхолда то удалять саба из мапы (считать его мертвым)

    private final EventService eventService;


    //todo need to implement event methods and staff for correcting and updating time for triggering event


    //todo this is a description of workflow for event time increase via notification
    /*
    event happens -> we want to postpone it -> click button for 30 extra minutes -> call about postponing this event comes ->
    |backend starts"| -> get all the events for this user with nextEventDate after the received event -> change nextEventDate to nextEventDate + 30 min and save to db ->
    -> send the notification that event was postponed successfully to all subs of this user
     */

    private final EventResponseMapper eventResponseMapper;

    private final Integer MAX_RETRIES_FOR_SUBSCRIBER = 100;

    private Map<Long, Map.Entry<List<EventDto>, Integer>> deliveryList = new HashMap<>();

    //should delete only few items from list for this sub
    private void removeDeliveredEvents(SubscriberEntity sub, EventDto eventDto){
        //todo check if performs or needs to be reassigned to older map
        Map.Entry<List<EventDto>, Integer> entry = deliveryList.get(sub.getId());
        entry.getKey().remove(eventDto);
        if (entry.getKey().size() == 0)
            deliveryList.remove(sub);
    }

    private void retryIncrease(SubscriberEntity sub){
        if (Objects.equals(deliveryList.get(sub.getId()).getValue(), MAX_RETRIES_FOR_SUBSCRIBER))
            deliveryList.remove(sub.getId());
        else
            deliveryList.get(sub.getId()).setValue(deliveryList.get(sub.getId()).getValue() + 1);
    }

    /*todo method should be concurrent, inside sendEvents should be invoked once every minute
    most likely timing should be reworked smh to not include seconds while checking for current event
    */
    //todo this method is only for testing, should be removed later
    public void beginNotificationSending(boolean enableSending){
        //todo there should be added method call to change next event times for sent events
        /*
        it seems that db should store how often event happens in minutes, nextEvent time is measured as current nextEvent.plusMinutes(minutesFromDb)
        should work fine but somehow i still need to determine how to skip checking seconds since they are irrelevant and could cause potential event loss
         */
        LocalDateTime placeholderTime = LocalDateTime.of(2000,1,1,1,1);
        //placeholderTime.plusMinutes(1);
        log.info("placeholder time " + placeholderTime);
        //todo placeholderTime should be changed to now()
        startEventNotificationChain(placeholderTime);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void beginSendingNotificationsOnStartup(){
        LocalDateTime placeholderTime = LocalDateTime.of(2000,1,1,1,1);
        //placeholderTime.plusMinutes(1);
        log.info("placeholder time " + placeholderTime);
        //todo placeholderTime should be changed to now()
        startEventNotificationChain(placeholderTime);
    }

    private void startEventNotificationChain(LocalDateTime currentTime){
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
        log.info("-----------------PAGMAN---------------");
        SubscriberEntity sub = (SubscriberEntity) objects.get(0);
        List<EventEntity> events = (List<EventEntity>) objects.get(1);
        log.info("+++ subs " + sub);
        log.info("+++ events" + events);
        deliveryList.put(sub.getId(),
                new AbstractMap.SimpleEntry<>(
                    events.stream().map(eventResponseMapper::mapEntityToResponse).collect(Collectors.toList()),
                    0
        ));
        sendEvents(sub, events);
    }

    private void sendEvents(SubscriberEntity sub, List<EventEntity> events){
        log.info("-----------------PAGMAN---------------");
        //SubscriberEntity sub = (SubscriberEntity) objects.get(0);
        //List<EventEntity> events = (List<EventEntity>) objects.get(1);
        log.info("+++ subs " + sub);
        log.info("+++ events" + events);

        for (EventEntity event: events) {
            retryIncrease(sub);
            sendEvent(sub,eventResponseMapper.mapEntityToResponse(event)).flatMap(health ->{
                if (health.isDeliverySuccessful())
                    removeDeliveredEvents(sub,eventResponseMapper.mapEntityToResponse(event));
                return Mono.just(health);
            }).subscribe();
        }
    }

    private SubscriberHealthCheckDto mapSubToFailedHC(SubscriberEntity subscriberEntity){
        SubscriberHealthCheckDto hc = new SubscriberHealthCheckDto();
        hc.toBuilder()
                .subscriberId(subscriberEntity.getId())
                .enabled(subscriberEntity.isEnabled())
                .userId(subscriberEntity.getUserId())
                .endpoint(subscriberEntity.getEndpoint())
                .deliverySuccessful(false)
                .build();
        return hc;
    }


    //todo consider moving it to other class since endpoint calls will be used multiple times, add onErrorResume to this method call
    public Mono<SubscriberHealthCheckDto> sendEvent(SubscriberEntity sub, EventDto eventDto){
        log.info("Starting sending events to subs\n-----------------------------------");

        try {
            //todo check response time, if more than 30 sec or response is bad or succ status isn't good then put the input dto to some kind of list and check always
            //todo sending should be reworked to be working with incorrect endpoints and shit
            return webClient
                    .post()
                    .uri(sub.getEndpoint())
                    .bodyValue(eventDto)
                    .retrieve()
                    .onStatus(httpStatusCode -> httpStatusCode.isError(), clientResponse -> Mono.error(new ApiException("bruh","400")))
                    .bodyToMono(SubscriberHealthCheckDto.class);
            //return Mono.just(mapSubToFailedHC(sub));
        }catch (RuntimeException e){
            log.error("Error occurred " + e.getMessage() + " error was caused by " + e.getCause());
            return Mono.just(mapSubToFailedHC(sub));
        }
    }
/*
    public Flux<SubscriberHealthCheckDto> healthcheckHeartbeat(){
        //todo call this every minute
        userService.findAll().flatMap(u -> subscriberRepository.getSubscriberEntitiesByUserId(u.getId())
                .flatMap(sub ->
                    checkHC(sub)//.flatMap(hc -> hc.isDeliverySuccessful() == false ? undeliveredHC.put(hc, 0) : undeliveredHC.remove(hc)  )
                ));
        while (hcRequired == true){
            checkHC();
        }
    }
*/

    public Mono<SubscriberResponseDto> subscribe(SubscriberRequestDto requestDto) {
        return subscriberRepository.findById(requestDto.getSubscriberId()).flatMap(subscriberEntity ->
            subscriberRepository.save(subscriberEntity
                                    .toBuilder()
                                    .enabled(true)
                                    .build())
        ).map(subscriberMapper::mapEntityToResponse);
    }

    public Mono<SubscriberResponseDto> unsubscribe(SubscriberRequestDto requestDto) {
        return subscriberRepository.findById(requestDto.getSubscriberId()).flatMap(subscriberEntity ->
                subscriberRepository.save(subscriberEntity
                                        .toBuilder()
                                        .enabled(false)
                                        .build())
        ).map(subscriberMapper::mapEntityToResponse);
    }

    public Mono<SubscriberResponseDto> create(SubscriberRequestDto requestDto) {
        return subscriberRepository.save(subscriberMapper.mapRequestToEntity(requestDto))
                .map(subscriberMapper::mapEntityToResponse);
    }

    public Mono<Void> delete(SubscriberRequestDto requestDto) {
        //Todo refactor to make it return smth that makes sense
        return subscriberRepository.deleteById(requestDto.getSubscriberId());
    }

    public Flux<SubscriberResponseDto> getSubscribersInfo(SubscriberRequestDto requestDto) {
        return subscriberRepository.getSubscriberEntitiesByUserId(requestDto.getUserId()).map(subscriberMapper::mapEntityToResponse);
    }
}
