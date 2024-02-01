package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.dto.SubscriberRequestDto;
import com.nikiko.timemanager.dto.SubscriberResponseDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.SubscriberEntity;
import com.nikiko.timemanager.mapper.EventMapper;
import com.nikiko.timemanager.mapper.SubscriberMapper;
import com.nikiko.timemanager.repository.SubscriberRepository;
import com.nikiko.timemanager.service.SubscriberNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
public class SubscriberServiceTest {
    @InjectMocks
    private SubscriberNotificationService subscriberNotificationService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Spy
    private SubscriberMapper subscriberMapper = Mappers.getMapper(SubscriberMapper.class);

    private SubscriberResponseDto subscriberResponseDto;

    private SubscriberRequestDto subscriberRequestDto;

    private SubscriberEntity subscriberEntity;
    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);
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
                .postponed(false)
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
        subscriberResponseDto = subscriberMapper.mapEntityToResponse(subscriberEntity);
        eventDto = eventMapper.mapEntityToResponse(eventEntity);
        eventRequestDto = eventMapper.mapFromEntityToRequest(eventEntity);
        Mockito.when(subscriberMapper.mapEntityToResponse(subscriberEntity)).thenReturn(subscriberResponseDto);
        Mockito.when(subscriberMapper.mapRequestToEntity(subscriberRequestDto)).thenReturn(subscriberEntity);
        Mockito.when(eventMapper.mapEntityToResponse(eventEntity)).thenReturn(eventDto);
        Mockito.when(eventMapper.mapFromRequestToEntity(eventRequestDto)).thenReturn(eventEntity);
//        System.out.println("another test " + subscriberMapper.mapRequestToEntity(subscriberRequestDto));
//        System.out.println("sub ent " + subscriberEntity);
//        System.out.println("srespdto " + subscriberMapper.mapEntityToResponse(subscriberEntity));
//        System.out.println("sec " + subscriberMapper.mapSmth(subscriberEntity));
//        System.out.println(" bruh ");
    }

    @Test
    @DisplayName("getSubscribersInfo returns Flux with subRespDto if successful")
    public void getSubscribersInfo_returnFluxSubRespDto_whenSuccessful(){
        BDDMockito.when(subscriberRepository.getSubscriberEntitiesByUserId(subscriberRequestDto.getUserId()))
                .thenReturn(Flux.just(subscriberEntity, subscriberEntity));
        StepVerifier.create(subscriberNotificationService.getSubscribersInfo(subscriberRequestDto.getUserId()))
                .expectSubscription()
                .expectNext(subscriberResponseDto)
                .expectNext(subscriberResponseDto)
                .verifyComplete();
    }
    @Test
    @DisplayName("delete returns Mono Void if successful")
    public void delete_returnMonoVoid_whenSuccessful(){
        BDDMockito.when(subscriberRepository.deleteById(subscriberRequestDto.getUserId()))
                .thenReturn(Mono.empty());
        StepVerifier.create(subscriberNotificationService.delete(subscriberRequestDto))
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }
    @Test
    @DisplayName("create returns MonoSubRespDto if successful")
    public void create_returnMonoSubRespDto_whenSuccessful(){
        BDDMockito.when(subscriberRepository.save(subscriberEntity))
                .thenReturn(Mono.just(subscriberEntity));
        StepVerifier.create(subscriberNotificationService.create(subscriberRequestDto))
                .expectSubscription()
                .expectNext(subscriberResponseDto)
                .verifyComplete();
    }
    @Test
    @DisplayName("unsubscribe returns MonoSubRespDto if successful")
    public void unsubscribe_returnMonoSubRespDto_whenSuccessful(){
        BDDMockito.when(subscriberRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(subscriberEntity));
        subscriberEntity.toBuilder()
                        .enabled(false)
                .build();
        BDDMockito.when(subscriberRepository.save(Mockito.any(SubscriberEntity.class)))
                .thenReturn(Mono.just(subscriberEntity));
        StepVerifier.create(subscriberNotificationService.unsubscribe(subscriberRequestDto))
                .expectSubscription()
                .expectNext(subscriberResponseDto)
                .verifyComplete();
    }
    @Test
    @DisplayName("subscribe returns MonoSubRespDto if successful")
    public void subscribe_returnMonoSubRespDto_whenSuccessful(){
        BDDMockito.when(subscriberRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(subscriberEntity));
        subscriberEntity.toBuilder()
                .enabled(true)
                .build();
        BDDMockito.when(subscriberRepository.save(Mockito.any(SubscriberEntity.class)))
                .thenReturn(Mono.just(subscriberEntity));
        StepVerifier.create(subscriberNotificationService.unsubscribe(subscriberRequestDto))
                .expectSubscription()
                .expectNext(subscriberResponseDto)
                .verifyComplete();
    }


//    private void exampleMethodInvoker(){
//        subscriberRepository.getSubscriberEntitiesByUserId(1L).flatMap(sub -> {
//                    sub.setSubscriberId(2L);
//                    return Mono.just(sub);
//                }
//        ).subscribe(this::exampleMethodeInvoked);
//    }
//
//    private void exampleMethodeInvoked(SubscriberEntity subscriberEntity) {
//        doSmth(subscriberEntity);
//    }
//
//    @Test
//    @DisplayName("test method")
//    public void sub(){
//        Mockito.when(subscriberNotificationService.)
//    }
}
