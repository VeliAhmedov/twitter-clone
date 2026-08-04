package com.twittvl.backend.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

//will use here, mapstruct library
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserResponse userToUserResponse(User user);

    void applyUpdate(UserUpdateRequest userUpdateRequest, @MappingTarget User user);
}
