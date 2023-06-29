package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.EventDto;
import com.nikiko.timemanager.entity.EventEntity;
import com.nikiko.timemanager.entity.EventEntity.EventEntityBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-21T11:36:21+1000",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.5 (SAP SE)"
)
@Component
public class EventResponseMapperImpl implements EventResponseMapper {

    @Override
    public EventDto mapEntityToResponse(EventEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EventDto eventDto = new EventDto();

        eventDto.setId( entity.getId() );
        eventDto.setName( entity.getName() );
        eventDto.setStartTime( entity.getStartTime() );
        eventDto.setDuration( entity.getDuration() );
        eventDto.setEnabled( entity.isEnabled() );
        eventDto.setShortDescription( entity.getShortDescription() );
        eventDto.setFullDescription( entity.getFullDescription() );
        eventDto.setCreatedAt( entity.getCreatedAt() );
        eventDto.setChangedAt( entity.getChangedAt() );
        eventDto.setNextEventTime( entity.getNextEventTime() );

        return eventDto;
    }

    @Override
    public EventEntity mapFromResponseToEntity(EventDto eventDto) {
        if ( eventDto == null ) {
            return null;
        }

        EventEntityBuilder eventEntity = EventEntity.builder();

        eventEntity.id( eventDto.getId() );
        eventEntity.name( eventDto.getName() );
        eventEntity.startTime( eventDto.getStartTime() );
        eventEntity.duration( eventDto.getDuration() );
        eventEntity.enabled( eventDto.isEnabled() );
        eventEntity.shortDescription( eventDto.getShortDescription() );
        eventEntity.fullDescription( eventDto.getFullDescription() );
        eventEntity.createdAt( eventDto.getCreatedAt() );
        eventEntity.changedAt( eventDto.getChangedAt() );
        eventEntity.nextEventTime( eventDto.getNextEventTime() );

        return eventEntity.build();
    }
}
