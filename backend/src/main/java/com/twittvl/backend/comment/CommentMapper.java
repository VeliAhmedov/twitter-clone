package com.twittvl.backend.comment;

import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "tweetId", source = "tweet.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "username", source = "user.username")
    CommentResponse toCommentResponse(Comment comment);

    void applyUpdate (CommentRequest commentRequest, @MappingTarget Comment comment);

}
