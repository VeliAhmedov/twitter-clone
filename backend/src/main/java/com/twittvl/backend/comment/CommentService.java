package com.twittvl.backend.comment;

import com.twittvl.backend.commentLike.CommentLikeRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.common.util.ServiceHelper;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentMapper commentMapper, CommentRepository commentRepository,
                          TweetRepository tweetRepository, UserRepository userRepository,
                          CommentLikeRepository commentLikeRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    //this does put response alongside updated like count
    private CommentResponse toCommentResponseWithLikeCount(Comment comment) {
        //how many likes does comment have
        long likeCount = commentLikeRepository.countByCommentId(comment.getId());
        //map entity then swap default 0 like with real number
        return commentMapper.toCommentResponse(comment).withLikeCount(likeCount);
    }

    //comment on tweet
    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest, Long userId, Long tweetId) {
        if (ServiceHelper.isBlank(commentRequest.content()) && ServiceHelper.isBlank(commentRequest.url())) {
            throw new IllegalArgumentException("comment can't be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id " + userId));
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("tweet not found with id " + tweetId));
        Comment comment = new Comment();
        comment.setTweet(tweet);
        comment.setUser(user);
        comment.setContent(commentRequest.content());
        comment.setImageUrl(ServiceHelper.isBlank(commentRequest.url()) ? null : commentRequest.url());
        Comment savedComment = commentRepository.save(comment);
        return toCommentResponseWithLikeCount(savedComment);
    }

    //replying to comment of tweet
    @Transactional
    public CommentResponse createReply(CommentRequest commentRequest, Long userId, Long parentCommentId) {
        if (ServiceHelper.isBlank(commentRequest.content()) && ServiceHelper.isBlank(commentRequest.url())) {
            throw new IllegalArgumentException("reply can't be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found with id " + userId));
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("parent comment not found with id " + parentCommentId));
        Comment reply = new Comment();
        reply.setUser(user);
        reply.setTweet(parentComment.getTweet());
        reply.setParentComment(parentComment);
        reply.setContent(commentRequest.content());
        reply.setImageUrl(ServiceHelper.isBlank(commentRequest.url()) ? null : commentRequest.url());
        Comment savedReply = commentRepository.save(reply);
        return toCommentResponseWithLikeCount(savedReply);
    }

    //getting replies to comment
    @Transactional(readOnly = true)
    public Page<CommentResponse> getRepliesByParentCommentId(Pageable pageable, Long parentCommentId) {
        return commentRepository.findAllByParentCommentIdOrderByCreatedAtDesc(parentCommentId, pageable)
                .map(this::toCommentResponseWithLikeCount);
    }

    //get comments on tweet
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTweedId(Pageable pageable, Long tweetId) {
        return commentRepository.findAllByTweetIdAndParentCommentIsNullOrderByCreatedAtDesc(tweetId, pageable)
                .map(this::toCommentResponseWithLikeCount);
    }

    //get comment
    @Transactional(readOnly = true)
    public CommentResponse getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comment not found with id " + commentId));
        return toCommentResponseWithLikeCount(comment);
    }

    //get comments on user's profile
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUserId(Pageable pageable, Long userId) {
        return commentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toCommentResponseWithLikeCount);
    }

    //edit that comment
    @Transactional
    public CommentResponse editComment(Long id, Long userId, CommentRequest commentRequest) {
        Comment comment = getOwnedComment(id, userId, "modify");
        boolean changed = !Objects.equals(comment.getContent(), commentRequest.content()) ||
                !Objects.equals(comment.getImageUrl(), commentRequest.url());
        commentMapper.applyUpdate(commentRequest, comment);
        if (changed) {
            comment.setEdited(true);
        }
        return toCommentResponseWithLikeCount(comment);
    }

    //delete comment
    @Transactional
    public void deleteComment(Long userId, Long tweetId) {
        Comment comment = getOwnedComment(tweetId, userId, "delete");
        commentRepository.delete(comment);
    }

    //helper method
    private Comment getOwnedComment(Long commentId, Long userId, String action) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found " + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("you can only " + action + " your own tweet");
        }
        return comment;
    }
}

