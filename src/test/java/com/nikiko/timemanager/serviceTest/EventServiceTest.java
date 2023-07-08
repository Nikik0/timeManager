package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.UserEntity;
import com.nikiko.timemanager.entity.UserRole;
import com.nikiko.timemanager.mapper.EventRequestMapper;
import com.nikiko.timemanager.mapper.EventResponseMapper;
import com.nikiko.timemanager.mapper.UserRequestMapper;
import com.nikiko.timemanager.mapper.UserResponseMapper;
import com.nikiko.timemanager.repository.EventRepository;
import com.nikiko.timemanager.repository.UserRepository;
import com.nikiko.timemanager.service.EventService;
import com.nikiko.timemanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Mock
    private EventRepository eventRepository;
    @Spy
    private EventResponseMapper eventResponseMapper = Mappers.getMapper(EventResponseMapper.class);
    @Spy
    private EventRequestMapper eventRequestMapper = Mappers.getMapper(EventRequestMapper.class);

    private final Long postponeMinutes = 30L;
    private EventEntity eventEntity;
    private EventDto eventDto;
    private EventRequestDto eventRequestDto;

    @BeforeEach
    public void setup(){
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
                .wasPostponed(false)
                .ownerId(1L)
                .nextEventTime(LocalDateTime.now().plusMinutes(30L))
                .lastHappened(LocalDateTime.now())
                .frequency(30L)
                .build();
        eventDto = eventResponseMapper.mapEntityToResponse(eventEntity);
        eventRequestDto = eventRequestMapper.mapFromEntityToRequest(eventEntity);
        Mockito.when(eventResponseMapper.mapEntityToResponse(eventEntity)).thenReturn(eventDto);
        Mockito.when(eventRequestMapper.mapFromRequestToEntity(eventRequestDto)).thenReturn(eventEntity);
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
        BDDMockito.when(eventRepository.save(Mockito.any(EventEntity.class)))
                .thenReturn(Mono.just(eventEntity));
        StepVerifier.create(eventService.change(eventRequestDto))
                .expectSubscription()
                .expectNext(eventDto)
                .verifyComplete();
    }
    @Test
    @DisplayName("getEventEntitiesByNextEventTime returns Flux with EventEntity if successful")
    public void getEventEntitiesByNextEventTime_returnFluxEventEntities_whenSuccessful(){
        BDDMockito.when(eventRepository.getEventEntitiesByNextEventTime(Mockito.any(LocalDateTime.class)))
                .thenReturn(Flux.just(eventEntity, eventEntity));
        StepVerifier.create(eventService.getEventEntitiesByNextEventTime(LocalDateTime.now()))
                .expectSubscription()
                .expectNext(eventEntity)
                .expectNext(eventEntity)
                .verifyComplete();
    }

    @Test
    @DisplayName("getEventEntitiesByOwnerIdAndNextEventTimeAfter returns Flux with EventEntity if successful")
    public void getEventEntitiesByOwnerIdAndNextEventTimeAfter_returnFluxEventEntities_whenSuccessful(){
        BDDMockito.when(eventRepository.getEventEntitiesByOwnerIdAndNextEventTimeAfter(ArgumentMatchers.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Flux.just(eventEntity, eventEntity));
        StepVerifier.create(eventService.getEventEntitiesByOwnerIdAndNextEventTimeAfter(1L, LocalDateTime.now()))
                .expectSubscription()
                .expectNext(eventEntity)
                .expectNext(eventEntity)
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
        BDDMockito.when(eventRepository.saveAll(Flux.just(eventEntity,eventEntity)))
                .thenReturn(Flux.just(eventEntity));
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
