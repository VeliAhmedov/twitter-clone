package com.twittvl.backend.comment;

import com.twittvl.backend.commentLike.CommentLikeRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.common.util.ServiceHelper;
import com.twittvl.backend.notification.NotificationProducer;
import com.twittvl.backend.notification.NotificationType;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentRepository commentRepository;
    private final NotificationProducer notificationProducer;
    private final CommentResponseAssembler commentResponseAssembler;

    public CommentService(CommentMapper commentMapper, CommentRepository commentRepository,
                          TweetRepository tweetRepository, UserRepository userRepository,
                          NotificationProducer notificationProducer, CommentResponseAssembler commentResponseAssembler) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.notificationProducer = notificationProducer;
        this.commentResponseAssembler = commentResponseAssembler;
    }

    //comment on tweet
    @Transactional
    public CommentResponse createComment(CommentRequest commentRequest, Long userId, Long tweetId) {
        if (ServiceHelper.isBlank(commentRequest.content()) && ServiceHelper.isBlank(commentRequest.url())) {
            throw new IllegalArgumentException("comment can't be empty");
        }

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("tweet not found with id " + tweetId));
        Comment comment = new Comment();
        comment.setTweet(tweet);
        comment.setUser(userRepository.getReferenceById(userId));
        comment.setContent(commentRequest.content());
        comment.setImageUrl(ServiceHelper.isBlank(commentRequest.url()) ? null : commentRequest.url());
        Comment savedComment = commentRepository.save(comment);
        //after commented, send notification
        notificationProducer.sendNotification(userId, tweet.getUser().getId(), NotificationType.COMMENT,
                tweetId, savedComment.getId());
        return commentResponseAssembler.toResponse(savedComment);
    }

    //replying to comment of tweet
    @Transactional
    public CommentResponse createReply(CommentRequest commentRequest, Long userId, Long parentCommentId) {
        if (ServiceHelper.isBlank(commentRequest.content()) && ServiceHelper.isBlank(commentRequest.url())) {
            throw new IllegalArgumentException("reply can't be empty");
        }

        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("parent comment not found with id " + parentCommentId));
        Comment reply = new Comment();
        reply.setUser(userRepository.getReferenceById(userId));
        reply.setTweet(parentComment.getTweet());
        reply.setParentComment(parentComment);
        reply.setContent(commentRequest.content());
        reply.setImageUrl(ServiceHelper.isBlank(commentRequest.url()) ? null : commentRequest.url());
        Comment savedReply = commentRepository.save(reply);
        //after replied, send notification with already created reply
        notificationProducer.sendNotification(userId, parentComment.getUser().getId(), NotificationType.REPLY,
                parentComment.getTweet().getId(), savedReply.getId());
        return commentResponseAssembler.toResponse(savedReply);
    }

    //getting replies to comment
    @Transactional(readOnly = true)
    public Page<CommentResponse> getRepliesByParentCommentId(Pageable pageable, Long parentCommentId) {
        return commentRepository.findAllByParentCommentIdOrderByCreatedAtDesc(parentCommentId, pageable)
                .map(commentResponseAssembler::toResponse);
    }

    //get comments on tweet
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTweedId(Pageable pageable, Long tweetId) {
        return commentRepository.findAllByTweetIdAndParentCommentIsNullOrderByCreatedAtDesc(tweetId, pageable)
                .map(commentResponseAssembler::toResponse);
    }

    //get comment
    @Transactional(readOnly = true)
    public CommentResponse getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comment not found with id " + commentId));
        return commentResponseAssembler.toResponse(comment);
    }

    //get comments on user's profile
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUserId(Pageable pageable, Long userId) {
        return commentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(commentResponseAssembler::toResponse);
    }

    //edit that comment
    @Transactional
    public CommentResponse editComment(Long id, Long userId, CommentRequest commentRequest) {
        Comment comment = getOwnedComment(id, userId, "modify");
        boolean changed =
                (commentRequest.content() != null && !Objects.equals(comment.getContent(), commentRequest.content())) ||
                (commentRequest.url() != null && !Objects.equals(comment.getImageUrl(), commentRequest.url()));
        //TODO: change url to imageURL
        commentMapper.applyUpdate(commentRequest, comment);
        if (changed) {
            comment.setEdited(true);
        }
        return commentResponseAssembler.toResponse(comment);
    }

    //delete comment
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = getOwnedComment(commentId, userId, "delete");
        commentRepository.delete(comment);
    }

    //helper method
    private Comment getOwnedComment(Long commentId, Long userId, String action) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found " + commentId));
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("you can only " + action + " your own comment");
        }
        return comment;
    }
}

