package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.UserRequestDto;
import com.nikiko.timemanager.entity.UserEntity;
import com.nikiko.timemanager.entity.UserEntity.UserEntityBuilder;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-15T13:45:12+1000",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.5 (SAP SE)"
)
@Component
public class UserRequestMapperImpl implements UserRequestMapper {

    @Override
    public UserEntity mapFromRequestToEntity(UserRequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.name( requestDto.getName() );
        userEntity.password( requestDto.getPassword() );

        return userEntity.build();
    }
}
