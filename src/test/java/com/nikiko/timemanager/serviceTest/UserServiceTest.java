package com.nikiko.timemanager.serviceTest;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import com.nikiko.timemanager.entity.UserRole;
import com.nikiko.timemanager.exception.ApiException;
import com.nikiko.timemanager.mapper.UserRequestMapper;
import com.nikiko.timemanager.mapper.UserResponseMapper;
import com.nikiko.timemanager.repository.UserRepository;
import com.nikiko.timemanager.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserResponseMapper userResponseMapper = Mappers.getMapper(UserResponseMapper.class);
    @Spy
    private UserRequestMapper userRequestMapper = Mappers.getMapper(UserRequestMapper.class);
    private UserEntity entity;
    private UserDto userDto;
    private UserRequestDto userRequestDto;
    @BeforeEach
    public void setup(){
        entity = new UserEntity(1L, "Reak", "qwerty", false, UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
        userDto = userResponseMapper.mapFromEntityToResponse(entity);
        userRequestDto = new UserRequestDto(entity.getId(), entity.getName(), entity.getPassword());
        Mockito.when(userResponseMapper.mapFromEntityToResponse(entity)).thenReturn(userDto);
        Mockito.when(userRequestMapper.mapFromRequestToEntity(userRequestDto)).thenReturn(entity);
    }

    @Test
    @DisplayName("findById returns Mono of UserDto if the user exists")
    public void findById_returnMonoUserDto_whenSuccessful(){
        BDDMockito.when(userRepository.findById(entity.getId()))
                .thenReturn(Mono.just(entity));
        StepVerifier.create(userService.findById(1L))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete();
    }
    @Test
    @DisplayName("findById returns Mono error if the user wasn't found")
    public void findById_returnMonoError_whenMonoEmpty(){
        BDDMockito.when(userRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Mono.empty());
        StepVerifier.create(userService.findById(1L))
                .expectSubscription()
                .expectError(ApiException.class)
                .verify();
    }

    @Test
    @DisplayName("save returns userDto when successful")
    public void save_returnUserDto_whenSuccessful(){
        BDDMockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(Mono.just(entity));
        StepVerifier.create(userService.save(userRequestDto))
                .expectSubscription()
                .expectNext(userDto)
                .verifyComplete();
    }

    @Test
    @DisplayName("change returns changed userDto when successful")
    public void change_returnUserDto_whenSuccessful(){
        userRequestDto.setName("Changed Name");
        userDto.setName(userRequestDto.getName());
        BDDMockito.when(userRepository.findById(entity.getId()))
                .thenReturn(Mono.just(entity));
        UserEntity entity_2 = entity.toBuilder().name(userRequestDto.getName()).build();
        BDDMockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(Mono.just(entity_2));
        StepVerifier.create(userService.change(userRequestDto))
                .expectSubscription()
                .consumeNextWith(dto -> {
                    assertEquals(dto.getId(), entity_2.getId());
                    assertEquals(dto.getName(), entity_2.getName());
                })
                .verifyComplete();
    }

}
