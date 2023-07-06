package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.SubscriberEntity;
import com.nikiko.timemanager.entity.UserEntity;
import com.nikiko.timemanager.entity.UserRole;
import com.nikiko.timemanager.mapper.UserRequestMapper;
import com.nikiko.timemanager.mapper.UserResponseMapper;
import com.nikiko.timemanager.mapper.UserResponseMapperImpl;
import com.nikiko.timemanager.repository.UserRepository;
import com.nikiko.timemanager.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@AllArgsConstructor
@Slf4j
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

//    @Mock
//    private UserResponseMapper userResponseMapper;
//    @Mock
//    private UserRequestMapper userRequestMapper;
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @MockBean
    private UserRequestMapper userRequestMapper;
    @MockBean
    private UserResponseMapper userResponseMapper;
    @MockBean
    private UserResponseMapperImpl userResponseMapperImp;
    UserRequestDto USER_1 = new UserRequestDto();
    UserRequestDto USER_2 = new UserRequestDto();
    UserRequestDto USER_3 = new UserRequestDto();
    private UserEntity entity;

    @BeforeEach
    public void setup(){
        entity = new UserEntity(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        USER_1.setId(1L);
        USER_1.setName("Neath");
        USER_1.setPassword("qwerty");
        USER_2.setId(100L);
        USER_2.setName("Nerw");
        USER_2.setPassword("qwerty321");
        USER_3.setId(1L);
        USER_3.setName("Neath");
        USER_3.setPassword("qwerty");
        //UserEntity userEntity_1 = userRequestMapper.mapFromRequestToEntity(USER_1);
        System.out.println(entity);
        BDDMockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(entity));
        BDDMockito.when(userRepository.save(entity))
                .thenReturn(Mono.just(entity));
        BDDMockito.when(userRepository.findAll())
                .thenReturn(Flux.just(entity));
        UserDto userDto = new UserDto(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(userResponseMapper.mapFromEntityToResponse(org.mockito.ArgumentMatchers.any())).thenReturn(userDto);
    }


    @Test
    public void findById_returnMonoUserDto_whenSuccessful() throws Exception{
        entity = new UserEntity(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        BDDMockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.just(entity));
       // UserDto userDto_1 = userResponseMapper.mapFromEntityToResponse(userRequestMapper.mapFromRequestToEntity(USER_1));
        UserDto userDto = new UserDto(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        Mockito.when(userResponseMapperImp.mapFromEntityToResponse(org.mockito.ArgumentMatchers.any())).thenReturn(userDto);
        Mockito.when(userResponseMapper.mapFromEntityToResponse(org.mockito.ArgumentMatchers.any())).thenReturn(userDto);

        UserResponseMapper mapper = Mappers.getMapper(UserResponseMapper.class);
        //Mockito.when(UserResponseMapper.INSTANCE.mapFromEntityToResponse(entity)).thenReturn(userDto);
        System.out.println(userDto);
        System.out.println(entity);
        System.out.println("call will be initiated");
        System.out.println("test map" + userRepository.findById(1L).map(userResponseMapperImp::mapFromEntityToResponse).block());
        System.out.println("called test " + userService.findByIdTest(1L).block());
        //System.out.println("called " + userService.findById(1L).block());
        System.out.println("call happened");
        StepVerifier.create(userRepository.findById(1L))
                .expectSubscription()
                .expectNext(entity)
                .verifyComplete();
    }
}
