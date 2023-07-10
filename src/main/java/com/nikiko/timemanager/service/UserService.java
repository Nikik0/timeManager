package com.nikiko.timemanager.service;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import com.nikiko.timemanager.entity.UserRole;
import com.nikiko.timemanager.exception.ApiException;
import com.nikiko.timemanager.mapper.UserRequestMapper;
import com.nikiko.timemanager.mapper.UserResponseMapper;
import com.nikiko.timemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRequestMapper requestMapper;
    private final UserResponseMapper responseMapper;
    private final UserRepository userRepository;

    public Mono<UserDto> save(UserRequestDto newUser){
        log.info("saving user "+ newUser.getId());
        return userRepository.save(requestMapper.mapFromRequestToEntity(newUser)
                    .toBuilder()
                    .userRole(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .blocked(false)
                    .build()
        ).map(responseMapper::mapFromEntityToResponse).switchIfEmpty(Mono.error(new ApiException("Failed to save event", "500")));
    }

    public Mono<UserDto> findById(Long id){
        return userRepository.findById(id).map(responseMapper::mapFromEntityToResponse).switchIfEmpty(Mono.error(new ApiException("User was not found", "404")));
    }

    public void delete(UserRequestDto userRequestDto){
        //TODO modify db to enable cascade deletion for all the entities depending on deleted user (so delete both events (? leave for logging purpose) and subs)
        userRepository.deleteById(userRequestDto.getId()).subscribe();
    }

    public Mono<UserDto> change(UserRequestDto user) {
        log.info("changing user " + user.getId());
        return userRepository.findById(user.getId()).flatMap(
                userEntity -> userRepository.save(
                        userEntity.toBuilder()
                                .name(user.getName())
                                .password(user.getPassword())
                                .updatedAt(LocalDateTime.now())
                                .build()
                )
        ).map(responseMapper::mapFromEntityToResponse);
    }

//    public Flux<UserDto> findAll(){
//        return userRepository.findAll().map(responseMapper::mapFromEntityToResponse);
//    }
}
