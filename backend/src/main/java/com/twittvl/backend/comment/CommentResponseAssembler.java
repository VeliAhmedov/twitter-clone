package com.twittvl.backend.comment;

import com.twittvl.backend.commentLike.CommentLikeRepository;
import org.springframework.stereotype.Component;

@Component
public class CommentResponseAssembler {
    private final CommentMapper commentMapper;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public CommentResponseAssembler(CommentMapper commentMapper, CommentLikeRepository commentLikeRepository,
                                    CommentRepository commentRepository) {
        this.commentMapper = commentMapper;
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
    }

    //this does put response alongside updated like count
    public CommentResponse toResponse(Comment comment) {
        //how many likes does comment or reply have
        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        //how many replies does comment or reply have
        long replyCount = commentRepository.countByParentCommentId(comment.getId());
        //map entity then swap default 0 like with real number
        return commentMapper.toCommentResponse(comment).withLikeCount(likeCount, replyCount);
    }
}
