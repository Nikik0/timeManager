package com.nikiko.timemanager.controller;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("timemanager/api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/current")
    public Mono<String> getCurrentEvent(){
        //todo only for testing, needs to be reworked later
        log.info("post call successful");
        return eventService.call();
    }

    @GetMapping("/testcall")
    public Mono<String> testingCall(){

        return Mono.just("call was successful");
    }

    @PostMapping("/get")
    public Mono<EventDto> getSingle(@RequestBody Long id){
        return eventService.getSingle(id);
    }

    @PostMapping("/all")
    public Flux<EventDto> getAll(@RequestBody UserRequestDto userRequestDto){
        return eventService.getAll(userRequestDto);
    }

    @PostMapping("/delete")
    public void deleteEvent(@RequestBody EventRequestDto requestDto){
        eventService.delete(requestDto);
    }

    @PostMapping("/change")
    public Mono<EventDto> changeEvent(@RequestBody EventRequestDto requestDto){
        return eventService.change(requestDto);
    }
    @PostMapping("/create")
    public Mono<EventDto> createEvent(@RequestBody EventRequestDto requestDto){
        return eventService.create(requestDto);
    }
    @PostMapping("/extend")
    public Mono<EventDto> extendEvent(@RequestBody EventRequestDto requestDto){
        return eventService.extend(requestDto);
    }
}
