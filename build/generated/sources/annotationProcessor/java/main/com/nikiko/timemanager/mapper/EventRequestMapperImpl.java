package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.EventRequestDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.EventEntity.EventEntityBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-30T14:25:40+1000",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.5 (SAP SE)"
)
@Component
public class EventRequestMapperImpl implements EventRequestMapper {

    @Override
    public EventEntity mapFromRequestToEntity(EventRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        EventEntityBuilder eventEntity = EventEntity.builder();

        eventEntity.id( requestDto.getId() );
        eventEntity.name( requestDto.getName() );
        eventEntity.startTime( requestDto.getStartTime() );
        eventEntity.duration( requestDto.getDuration() );
        eventEntity.enabled( requestDto.isEnabled() );
        eventEntity.shortDescription( requestDto.getShortDescription() );
        eventEntity.fullDescription( requestDto.getFullDescription() );

        return eventEntity.build();
    }

    @Override
    public EventRequestDto mapFromEntityToRequest(EventEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EventRequestDto eventRequestDto = new EventRequestDto();

        eventRequestDto.setId( entity.getId() );
        eventRequestDto.setName( entity.getName() );
        eventRequestDto.setStartTime( entity.getStartTime() );
        eventRequestDto.setDuration( entity.getDuration() );
        eventRequestDto.setEnabled( entity.isEnabled() );
        eventRequestDto.setShortDescription( entity.getShortDescription() );
        eventRequestDto.setFullDescription( entity.getFullDescription() );

        return eventRequestDto;
    }
}
