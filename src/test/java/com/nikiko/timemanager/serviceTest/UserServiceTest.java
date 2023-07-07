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
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserResponseMapper userResponseMapper = Mappers.getMapper(UserResponseMapper.class);
    private UserEntity entity;
    private UserDto userDto;
    @BeforeEach
    public void setup(){
        entity = new UserEntity(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());;
        userDto = userResponseMapper.mapFromEntityToResponse(entity);
        Mockito.when(userResponseMapper.mapFromEntityToResponse(entity)).thenReturn(userDto);
    }

    @Test
    public void findById_returnMonoUserDto_whenSuccessful() throws Exception{
        BDDMockito.when(userRepository.findById(entity.getId()))
                .thenReturn(Mono.just(entity));
        System.out.println("call happened");
        System.out.println("entity is " + entity);
        System.out.println("dto is " + userDto);
        StepVerifier.create(userService.findById(1L))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete();
    }
}
