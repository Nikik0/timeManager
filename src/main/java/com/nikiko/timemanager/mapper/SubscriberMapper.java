package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.SubscriberRequestDto;
import com.nikiko.timemanager.dto.SubscriberResponseDto;
import com.nikiko.timemanager.entity.SubscriberEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriberMapper {
    SubscriberEntity mapRequestToEntity(SubscriberRequestDto requestDto);
    SubscriberResponseDto mapEntityToResponse(SubscriberEntity entity);
}
