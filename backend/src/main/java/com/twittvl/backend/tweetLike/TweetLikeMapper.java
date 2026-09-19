package com.twittvl.backend.tweetLike;

import com.twittvl.backend.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TweetLikeMapper {
    TweetLikeUserResponse userToTweetLikeUserResponse(User user);
}
