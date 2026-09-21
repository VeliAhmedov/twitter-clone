package com.twittvl.backend.comment;

import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "tweetId", source = "tweet.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "likeCount", ignore = true)
    CommentResponse toCommentResponse(Comment comment);

    void applyUpdate (CommentRequest commentRequest, @MappingTarget Comment comment);

}
