package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.AbstractQueue;

@Mapper(componentModel = "spring")
public interface UserRequestMapper {
    UserEntity mapFromRequestToEntity(UserRequestDto requestDto);
  //  @InheritInverseConfiguration
   // UserRequestDto mapFromEntityToRequest(UserEntity userEntity);
}
