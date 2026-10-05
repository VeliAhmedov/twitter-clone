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

    public CommentResponse toResponse(Comment comment) {
        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        long replyCount = commentRepository.countByParentCommentId(comment.getId());
        return commentMapper.toCommentResponse(comment).withLikeCount(likeCount, replyCount);
    }
}
