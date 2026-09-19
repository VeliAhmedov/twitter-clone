package com.twittvl.backend.commentLike;

public record CommentLikeUserResponse(
        Long id,
        String username,
        String displayName,
        String avatarURL
) {
}
