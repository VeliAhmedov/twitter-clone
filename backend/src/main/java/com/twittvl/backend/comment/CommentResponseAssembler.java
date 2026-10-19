package com.twittvl.backend.comment;

import com.twittvl.backend.bookmark.BookmarkRepository;
import com.twittvl.backend.commentLike.CommentLikeRepository;
import com.twittvl.backend.retweet.RetweetRepository;
import com.twittvl.backend.security.CurrentUser;
import org.springframework.stereotype.Component;

@Component
public class CommentResponseAssembler {
    private final CommentMapper commentMapper;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final RetweetRepository retweetRepository;
    private final BookmarkRepository bookmarkRepository;

    public CommentResponseAssembler(CommentMapper commentMapper, CommentLikeRepository commentLikeRepository,
                                    CommentRepository commentRepository, RetweetRepository retweetRepository,
                                    BookmarkRepository bookmarkRepository) {
        this.commentMapper = commentMapper;
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
        this.retweetRepository = retweetRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    //this does put response alongside updated like count
    public CommentResponse toResponse(Comment comment) {
        //how many likes does comment or reply have
        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        //how many replies does comment or reply have
        long replyCount = commentRepository.countByParentCommentId(comment.getId());
        //map entity then swap default 0 like with real number
        long retweetCount = retweetRepository.countByCommentId(comment.getId());
        boolean liked = false, retweeted = false, bookmarked = false;
        //because unlike tweet, we do limit area of viewer, it is better to add alongside response instead of fully integrated to service class
        Long viewerId = CurrentUser.idOrNull();
        if (viewerId != null) {
            liked = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), viewerId);
            retweeted = retweetRepository.existsByUserIdAndCommentId(viewerId, comment.getId());
            bookmarked = bookmarkRepository.existsByUserIdAndCommentId(viewerId, comment.getId());
        }
        return commentMapper.toCommentResponse(comment)
                .withCounts(likeCount, replyCount, retweetCount)
                .withLikedByMe(liked, retweeted, bookmarked);
    }
}
