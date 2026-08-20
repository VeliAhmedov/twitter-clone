package com.twittvl.backend.tweet;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TweetMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatarUrl", source = "user.avatarUrl")
    TweetResponse tweetToTweetResponse(Tweet tweet);

    void applyUpdate (TweetRequest tweetRequest, @MappingTarget Tweet tweet);
}
