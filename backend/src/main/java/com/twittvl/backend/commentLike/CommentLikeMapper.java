package com.twittvl.backend.commentLike;

import com.twittvl.backend.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentLikeMapper {
    CommentLikeUserResponse userToCommentLikeUserResponse(User user);
}
