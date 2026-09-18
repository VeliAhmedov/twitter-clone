package com.twittvl.backend.commentLike;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweetLike.TweetLike;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService {
    private CommentLikeRepository commentLikeRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    public CommentLikeService(CommentLikeRepository commentLikeRepository, UserRepository userRepository,  CommentRepository commentRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    //like comment, return long to increase amount when liked
    @Transactional
    public long likeComment(long userId, long commentId) {
        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new IllegalArgumentException("Tweet with id " + commentId + " is already liked");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + commentId + " not found"));
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
    public long unlikeComment(long userId, long commentId) {
        CommentLike  commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + commentId + " not found"));
        commentLikeRepository.delete(commentLike);
        return commentLikeRepository.countByCommentId(commentId);
    }

}
