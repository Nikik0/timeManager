package com.nikiko.timemanager.mapper;

import com.nikiko.timemanager.dto.UserDto;
import com.nikiko.timemanager.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-02T15:37:41+1000",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 17.0.5 (SAP SE)"
)
@Component
public class UserResponseMapperImpl implements UserResponseMapper {

    @Override
    public UserDto mapFromEntityToResponse(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( userEntity.getId() );
        userDto.setName( userEntity.getName() );
        userDto.setPassword( userEntity.getPassword() );
        userDto.setBlocked( userEntity.isBlocked() );
        userDto.setUserRole( userEntity.getUserRole() );
        userDto.setCreatedAt( userEntity.getCreatedAt() );
        userDto.setUpdatedAt( userEntity.getUpdatedAt() );

        return userDto;
    }
}
