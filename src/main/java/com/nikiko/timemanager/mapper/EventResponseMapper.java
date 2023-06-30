package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.entity.EventEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventResponseMapper {
    EventDto mapEntityToResponse(EventEntity entity);
    @InheritInverseConfiguration
    EventEntity mapFromResponseToEntity(EventDto eventDto);
}
