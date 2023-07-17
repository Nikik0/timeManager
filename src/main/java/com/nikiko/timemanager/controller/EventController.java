package com.nikiko.timemanager.controller;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.exception.ApiException;
import com.nikiko.timemanager.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("timemanager/api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/get")
    public Mono<EventDto> getSingle(@RequestBody UserRequestDto userRequestDto){
        return eventService.getSingle(userRequestDto.getId());
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
        if (requestDto.getId() != null) return Mono.error(new ApiException("Id should be missing", "400"));
        return eventService.create(requestDto);
    }
    @PostMapping("/postpone")
    public Mono<EventDto> postpone(@RequestBody EventRequestDto eventRequestDto){
        return eventService.postponeEventBetween(eventRequestDto, LocalDateTime.now());
    }
}
