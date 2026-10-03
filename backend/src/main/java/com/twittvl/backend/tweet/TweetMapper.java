package com.twittvl.backend.tweet;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TweetMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    TweetResponse tweetToTweetResponse(Tweet tweet);

    @Mapping(target = "imageUrl", source = "image")
    @Mapping(target = "quotedTweet", ignore = true)
    void applyUpdate (TweetRequest tweetRequest, @MappingTarget Tweet tweet);
}
