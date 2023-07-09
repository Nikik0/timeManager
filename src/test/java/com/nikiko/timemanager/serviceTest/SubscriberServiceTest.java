package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.SubscriberRequestDto;
import com.nikiko.timemanager.dto.SubscriberResponseDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.SubscriberEntity;
import com.nikiko.timemanager.mapper.EventRequestMapper;
import com.nikiko.timemanager.mapper.EventResponseMapper;
import com.nikiko.timemanager.mapper.SubscriberMapper;
import com.nikiko.timemanager.repository.SubscriberRepository;
import com.nikiko.timemanager.service.SubscriberNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
public class SubscriberServiceTest {
    @InjectMocks
    private SubscriberNotificationService subscriberNotificationService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Spy
    private SubscriberMapper subscriberMapper;

    private SubscriberResponseDto subscriberResponseDto;

    private SubscriberRequestDto subscriberRequestDto;

    private SubscriberEntity subscriberEntity;
    @Spy
    private EventResponseMapper eventResponseMapper;
    @Spy
    private EventRequestMapper eventRequestMapper;
    private EventEntity eventEntity;
    private EventRequestDto eventRequestDto;
    private EventDto eventDto;

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
        subscriberEntity = new SubscriberEntity().toBuilder()
                .subscriberId(1L)
                .userId(1L)
                .enabled(true)
                .endpoint("testEndpoint.com/sub")
                .build();
        subscriberRequestDto = new SubscriberRequestDto().toBuilder()
                .subscriberId(subscriberEntity.getSubscriberId())
                .userId(subscriberEntity.getUserId())
                .enabled(subscriberEntity.isEnabled())
                .endpoint(subscriberEntity.getEndpoint())
                .build();
        Mockito.when(subscriberMapper.mapEntityToResponse(subscriberEntity)).thenReturn(subscriberResponseDto);
        Mockito.when(subscriberMapper.mapRequestToEntity(subscriberRequestDto)).thenReturn(subscriberEntity);
        Mockito.when(eventResponseMapper.mapEntityToResponse(eventEntity)).thenReturn(eventDto);
        Mockito.when(eventRequestMapper.mapFromRequestToEntity(eventRequestDto)).thenReturn(eventEntity);
        subscriberResponseDto = subscriberMapper.mapEntityToResponse(subscriberEntity);
        eventDto = eventResponseMapper.mapEntityToResponse(eventEntity);
        eventRequestDto = eventRequestMapper.mapFromEntityToRequest(eventEntity);
        System.out.println("sub ent " + subscriberEntity);
        System.out.println("srespdto " + subscriberResponseDto);
    }

    @Test
    @DisplayName("create returns Mono EventDto if successful")
    public void getSubscribersInfo_returnFluxSubRespDto_whenSuccessful(){
        BDDMockito.when(subscriberRepository.getSubscriberEntitiesByUserId(subscriberRequestDto.getUserId()))
                .thenReturn(Flux.just(subscriberEntity, subscriberEntity));
        System.out.println("from repo " + subscriberResponseDto);//subscriberMapper.mapEntityToResponse(subscriberEntity));//subscriberRepository.getSubscriberEntitiesByUserId(subscriberRequestDto.getUserId()).map(subscriberMapper::mapEntityToResponse).blockLast());
//        StepVerifier.create(subscriberNotificationService.getSubscribersInfo(subscriberRequestDto))
//                .expectSubscription()
//                .expectNext(subscriberResponseDto)
//                .expectNext(subscriberResponseDto)
//                .verifyComplete();
    }
}
