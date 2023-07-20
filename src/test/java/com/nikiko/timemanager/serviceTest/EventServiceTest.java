package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.mapper.EventMapper;
import com.nikiko.timemanager.repository.EventRepository;
import com.nikiko.timemanager.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);


    private final Long postponeMinutes = 30L;
    private EventEntity eventEntity;
    private EventDto eventDto;
    private EventRequestDto eventRequestDto;

    @BeforeEach
    public void setup(){
        ReflectionTestUtils.setField(eventService, "postponeMinutes", postponeMinutes);
        eventEntity = new EventEntity().toBuilder()
                .id(1L)
                .name("Test Event")
                .startTime(LocalDateTime.now())
                .duration(20L)
                .shortDescription("short")
                .fullDescription("long")
                .createdAt(LocalDateTime.now())
                .changedAt(LocalDateTime.now())
                .startTime(LocalDateTime.now())
                .postponed(false)
                .ownerId(1L)
                .nextEventTime(LocalDateTime.now().plusMinutes(30L))
                .lastHappened(LocalDateTime.now())
                .frequency(30L)
                .build();
        eventDto = eventMapper.mapEntityToResponse(eventEntity);
        eventRequestDto = eventMapper.mapFromEntityToRequest(eventEntity);
        Mockito.when(eventMapper.mapEntityToResponse(eventEntity)).thenReturn(eventDto);
        Mockito.when(eventMapper.mapFromRequestToEntity(eventRequestDto)).thenReturn(eventEntity);
    }

    @Test
    @DisplayName("create returns Mono EventDto if successful")
    public void create_returnMonoEventDto_whenSuccessful(){
        BDDMockito.when(eventRepository.save(Mockito.any(EventEntity.class)))
                .thenReturn(Mono.just(eventEntity));
        StepVerifier.create(eventService.create(eventRequestDto))
                .expectSubscription()
                .expectNext(eventDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("getSingle returns Mono EventDto if successful")
    public void getSingle_returnMonoEventDto_whenSuccessful(){
        BDDMockito.when(eventRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(eventEntity));
        StepVerifier.create(eventService.getSingle(1L))
                .expectSubscription()
                .expectNext(eventDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("getAll returns Flux with EventDtos if successful")
    public void getAll_returnFluxEventDtos_whenSuccessful(){
        BDDMockito.when(eventRepository.getEventEntitiesByOwnerId(ArgumentMatchers.anyLong()))
                .thenReturn(Flux.just(eventEntity, eventEntity));
        StepVerifier.create(eventService.getAll(new UserRequestDto(1L,"Test owner", "pass")))
                .expectSubscription()
                .expectNext(eventDto)
                .expectNext(eventDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("saveEntity returns Mono EventEntity if successful")
    public void saveEntity_returnMonoEventEntity_whenSuccessful(){
        BDDMockito.when(eventRepository.save(Mockito.any(EventEntity.class)))
                .thenReturn(Mono.just(eventEntity));
        StepVerifier.create(eventService.saveEntity(eventEntity))
                .expectSubscription()
                .expectNext(eventEntity)
                .verifyComplete();
    }

    @Test
    @DisplayName("change returns Mono EventDto if successful")
    public void change_returnMonoEventDto_whenSuccessful(){
        BDDMockito.when(eventRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(eventEntity));
        EventEntity entity = eventEntity.toBuilder()
                .name("changed name")
                .build();
        eventRequestDto.setName(entity.getName());
        BDDMockito.when(eventRepository.save(Mockito.any(EventEntity.class)))
                .thenReturn(Mono.just(entity));
        StepVerifier.create(eventService.change(eventRequestDto))
                .expectSubscription()
                .consumeNextWith(dto -> {
                    assertEquals(dto.getId(), entity.getId());
                    assertEquals(dto.getName(), entity.getName());
                })
                .verifyComplete();
    }


    //todo this test should be more meaningful
    @Test
    @DisplayName("postponeEventBetween returns Mono EventDto if successful")
    public void postponeEventBetween_returnMonoEventDto_whenSuccessful(){
        BDDMockito.when(eventRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(eventEntity));
        BDDMockito.when(eventRepository.getEventEntitiesByOwnerIdAndNextEventTimeBetween(ArgumentMatchers.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Flux.just(eventEntity, eventEntity));
        BDDMockito.when(eventRepository.saveAll(Mockito.any(Flux.class)))
                .thenReturn(Flux.just(eventEntity));
        BDDMockito.when(eventRepository.save(Mockito.any(EventEntity.class))).thenReturn(Mono.just(eventEntity));
        StepVerifier.create(eventService.postponeEventBetween(eventRequestDto, LocalDateTime.now()))
                .expectSubscription()
                .expectNext(eventDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("delete returns Mono Void if successful")
    public void delete_returnMonoVoid_whenSuccessful(){
        BDDMockito.when(eventRepository.deleteById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());
        StepVerifier.create(eventService.delete(eventRequestDto))
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }
}
