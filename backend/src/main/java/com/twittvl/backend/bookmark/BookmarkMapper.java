package com.twittvl.backend.bookmark;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookmarkMapper {
    @Mapping(target = "tweetId", source = "tweet.id")
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "displayName", source = "user.displayName")
    BookmarkResponse toBookmarkResponse(Bookmark bookmark);
}
