package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.Comment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TweetMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userDisplayName", source = "user.displayName")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "quoteUnavailable", source = "tweet", qualifiedByName = "quoteUnavailable") //tell name of method
    TweetResponse tweetToTweetResponse(Tweet tweet);

    //add image too here
    @Mapping(target = "imageUrl", source = "image")
    @Mapping(target = "quotedTweet", ignore = true)
    @Mapping(target = "quotedComment", ignore = true)
    @Mapping(target = "quote", ignore = true)
    void applyUpdate(TweetRequest tweetRequest, @MappingTarget Tweet tweet);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userDisplayName", source = "user.displayName")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    QuotedTweetResponse toQuotedTweetResponse(Tweet tweet);

    @Mapping(target = "tweetId", source = "tweet.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userDisplayName", source = "user.displayName")
    @Mapping(target = "userAvatarUrl", source = "user.avatarURL")
    QuotedCommentResponse toQuotedCommentResponse(Comment comment);

    //instead of using expression of MapStruct, default method
    //this tweet was quote and both comment/tweet referance is null which means quoted tweet/comment is deleted
    //name of method that will be used instead of expression
    @Named("quoteUnavailable")
    default boolean isQuoteUnavailable(Tweet tweet) {
        return tweet.isQuote()
                && tweet.getQuotedTweet() == null
                && tweet.getQuotedComment() == null;
    }
}

//target is return type output (we want to convert to), field name from  TweetResponse DTO record
//source is parameter input (we convert from), field name from Tweet entity class
//for nested field like userId which is inside User entity class field inside tweet entity class
//we do write user.id

//1.in normal tweet, quote is false (not quote tweet), both quote comment/tweet is also null so quoteUnavailable is false
//2.in quote tweet and original quoted alive, quote is true, one of quote comment/tweet is set so quoteUnavailable is false
//3.in quote tweet and original quoted deleted, quote is true, both quote comment/tweet is also null so quoteUnavailable is true