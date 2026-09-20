package com.twittvl.backend.comment;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long tweetId,
        Long userId,
        Long parentCommentId,
        String username,
        String displayName,
        String userAvatarUrl,
        String content,
        String imageUrl,
        boolean edited,
        Instant createdAt
) {
}

//TODO : alongside username, also add displayName as well to show both
