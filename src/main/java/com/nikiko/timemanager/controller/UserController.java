package com.nikiko.timemanager.controller;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.exception.ApiException;
import com.nikiko.timemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("timemanager/api/v1/customers")
public class UserController {
    private final UserService userService;

    @GetMapping("/info/{id}")
    public Mono<UserDto> getCustomer(@PathVariable Long id){
        return userService.findById(id);
    }

    @PostMapping("/create")
    public Mono<UserDto> create(@RequestBody UserRequestDto requestDto){
        if (requestDto.getId() != null) return Mono.error(new ApiException("Id should be missing", "400"));
        return userService.save(requestDto);
    }

    @PostMapping("/delete")
    public void delete(@RequestBody UserRequestDto requestDto){
        log.info(requestDto.toString());
        userService.delete(requestDto);
    }

    @PostMapping("/change")
    public Mono<UserDto> change(@RequestBody UserRequestDto userDto){
        return userService.change(userDto);
    }
}
