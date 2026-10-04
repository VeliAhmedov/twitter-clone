package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TweetMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    TweetResponse tweetToTweetResponse(Tweet tweet);

    //add image too here
    @Mapping(target = "imageUrl", source = "image")
    @Mapping(target = "quotedTweet", ignore = true)
    void applyUpdate (TweetRequest tweetRequest, @MappingTarget Tweet tweet);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "quotedComment", ignore = true)
    QuotedTweetResponse toQuotedTweetResponse(Tweet tweet);

    @Mapping(target = "tweetId", source = "tweet.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    QuotedCommentResponse toQuotedCommentResponse(Comment comment);
}

//target is return type output (we want to convert to), field name from  TweetResponse DTO record
//source is parameter input (we convert from), field name from Tweet entity class
//for nested field like userId which is inside User entity class field inside tweet entity class
//we do write user.id