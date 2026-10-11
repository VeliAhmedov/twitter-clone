package com.twittvl.backend.comment;

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

    public CommentResponseAssembler(CommentMapper commentMapper, CommentLikeRepository commentLikeRepository,
                                    CommentRepository commentRepository, RetweetRepository retweetRepository) {
        this.commentMapper = commentMapper;
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
        this.retweetRepository = retweetRepository;
    }

    //this does put response alongside updated like count
    public CommentResponse toResponse(Comment comment) {
        //how many likes does comment or reply have
        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        //how many replies does comment or reply have
        long replyCount = commentRepository.countByParentCommentId(comment.getId());
        //map entity then swap default 0 like with real number
        long retweetCount = retweetRepository.countByCommentId(comment.getId());
        //because unlike tweet, we do limit area of viewer, it is better to add alongside response instead of fully integrated to service class
        Long viewerId = CurrentUser.idOrNull();
        boolean likedByMe = viewerId != null
                && commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), viewerId);
        return commentMapper.toCommentResponse(comment).withCounts(likeCount, replyCount, retweetCount).withLikedByMe(likedByMe);
    }
}
