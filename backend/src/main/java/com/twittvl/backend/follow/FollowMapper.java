package com.twittvl.backend.follow;

import com.twittvl.backend.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FollowMapper {
    FollowUserResponse userToFollowUserResponse(User user);
}
