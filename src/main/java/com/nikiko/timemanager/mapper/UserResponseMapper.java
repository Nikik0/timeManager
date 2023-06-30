package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
   // UserEntity mapFromResponseToEntity(UserDto userDto);
    @InheritInverseConfiguration
    UserDto mapFromEntityToResponse(UserEntity userEntity);
}
