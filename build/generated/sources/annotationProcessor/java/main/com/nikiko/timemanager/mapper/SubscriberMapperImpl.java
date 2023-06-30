package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.SubscriberRequestDto;
import com.nikiko.timemanager.dto.SubscriberResponseDto;
import com.nikiko.timemanager.entity.SubscriberEntity;
import com.nikiko.timemanager.entity.SubscriberEntity.SubscriberEntityBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-14T19:30:09+1000",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.5 (SAP SE)"
)
@Component
public class SubscriberMapperImpl implements SubscriberMapper {

    @Override
    public SubscriberEntity mapRequestToEntity(SubscriberRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        SubscriberEntityBuilder subscriberEntity = SubscriberEntity.builder();

        subscriberEntity.endpoint( requestDto.getEndpoint() );
        subscriberEntity.userId( requestDto.getUserId() );
        subscriberEntity.enabled( requestDto.isEnabled() );

        return subscriberEntity.build();
    }

    @Override
    public SubscriberResponseDto mapEntityToResponse(SubscriberEntity entity) {
        if ( entity == null ) {
            return null;
        }

        SubscriberResponseDto subscriberResponseDto = new SubscriberResponseDto();

        return subscriberResponseDto;
    }
}
