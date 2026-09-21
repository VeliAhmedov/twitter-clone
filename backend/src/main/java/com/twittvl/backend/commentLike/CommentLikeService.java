package com.twittvl.backend.commentLike;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeMapper commentLikeMapper;
    public CommentLikeService(CommentLikeRepository commentLikeRepository, UserRepository userRepository,  CommentRepository commentRepository,  CommentLikeMapper commentLikeMapper) {
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentLikeMapper = commentLikeMapper;
    }

    //like comment, return long to increase amount when liked
    @Transactional
    public long likeComment(Long userId, Long commentId) {
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("Comment with id " + commentId + " is already liked");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + commentId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        CommentLike commentLike = new CommentLike();
        commentLike.setUser(user);
        commentLike.setComment(comment);
        commentLikeRepository.save(commentLike);
        return commentLikeRepository.countByCommentId(commentId);
    }

    //unlike comment, return long to decrease amount when liked
    @Transactional
    public long unlikeComment(Long userId, Long commentId) {
        CommentLike  commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Like not found for comment " + commentId + " and user " + userId));        commentLikeRepository.delete(commentLike);
        return commentLikeRepository.countByCommentId(commentId);
    }

    @Transactional(readOnly = true)
    public Page<CommentLikeUserResponse> getCommentLikers(Long commentId, Pageable pageable) {
        return commentLikeRepository.findAllByCommentIdOrderByCreatedAtDesc(commentId, pageable)
                .map(commentLike -> commentLikeMapper.userToCommentLikeUserResponse(commentLike.getUser()));
    }
}
