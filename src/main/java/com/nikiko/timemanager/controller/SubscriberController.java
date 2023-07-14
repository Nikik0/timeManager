package com.nikiko.timemanager.controller;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.SubscriberRequestDto;
import com.nikiko.timemanager.dto.SubscriberResponseDto;
import com.nikiko.timemanager.service.EventService;
import com.nikiko.timemanager.service.SubscriberNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("timemanager/api/v1/subscribers")
@RequiredArgsConstructor
@Slf4j
public class SubscriberController {

    private final SubscriberNotificationService notificationService;
    //private final EventService eventService;

    @PostMapping("/subscribe")
    public Mono<SubscriberResponseDto> subscribe(@RequestBody SubscriberRequestDto requestDto){
        return notificationService.subscribe(requestDto);
    }
    @PostMapping("/unsubscribe")
    public Mono<SubscriberResponseDto> unsubscribe(@RequestBody SubscriberRequestDto requestDto){
        return notificationService.unsubscribe(requestDto);
    }

    @PostMapping("/create")
    public Mono<SubscriberResponseDto> create(@RequestBody SubscriberRequestDto requestDto){
        return notificationService.create(requestDto);
    }

    @PostMapping("/delete")
    public Mono<Void> delete(@RequestBody SubscriberRequestDto requestDto){
        return notificationService.delete(requestDto);
    }
    @PostMapping("/info")
    public Flux<SubscriberResponseDto> getSubscribersInfo(@RequestBody SubscriberRequestDto requestDto){
        return notificationService.getSubscribersInfo(requestDto);
    }

//    @PostMapping("/test")
//    public Mono<String> testCall() throws InterruptedException {
//        notificationService.beginNotificationSending(true);
//        return Mono.just("Test call in sub controller successful");
//    }


}
