package com.twittvl.backend.follow;

import com.twittvl.backend.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface FollowMapper {
    FollowUserResponse userToFollowUserResponse(User user);
}
