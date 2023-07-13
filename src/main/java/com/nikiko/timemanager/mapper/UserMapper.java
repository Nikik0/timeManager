package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity mapFromRequestToEntity(UserRequestDto requestDto);
    @InheritInverseConfiguration
    UserDto mapFromEntityToResponse(UserEntity userEntity);
}
