package com.twittvl.backend.bookmark;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.user.User;
import org.mapstruct.Mapper;

//used mapper class instead of MapStruct because use of expression of mapstruct
//make testing complicated for future testing and quality checking
@Mapper(componentModel = "spring")
public interface BookmarkMapper {

    default BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        Tweet tweet = new Tweet();
        Comment comment = new Comment();

        Long tweetId = null;
        Long commentId = null;
        User user;
        String content;
        String imageUrl;

        if (tweet != null) {
            tweetId = tweet.getId();
            user = tweet.getUser();
            content = tweet.getContent();
            imageUrl = tweet.getImageUrl();
        } else {
            commentId = comment.getId();
            user = comment.getUser();
            content = comment.getContent();
            imageUrl = comment.getImageUrl();
        }
        return new BookmarkResponse(
                bookmark.getId(),
                tweetId,
                commentId,
                user.getUsername(),
                user.getDisplayName(),
                content,
                imageUrl,
                bookmark.getCreatedAt()
        );
    }
}
