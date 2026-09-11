package com.twittvl.backend.comment;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentRepository commentRepository;

    public CommentService(CommentMapper commentMapper, CommentRepository commentRepository, TweetRepository tweetRepository, UserRepository userRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Transactional()
    public CommentResponse createComment(CommentRequest commentRequest, Long userId, Long tweetId) {
        if (isBlank(commentRequest.content()) && isBlank(commentRequest.url())) {
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
        comment.setImageUrl(commentRequest.url());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponse(savedComment);

    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTweedId(Pageable pageable, Long tweetId) {
        return commentRepository.findAllByTweetIdOrderByCreatedAtDesc(tweetId, pageable)
                .map(commentMapper::toCommentResponse);
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comment not found with id " + commentId));
        return commentMapper.toCommentResponse(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUserId(Pageable pageable, Long userId) {
        return commentRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(commentMapper::toCommentResponse);
    }
    private boolean isBlank(String content) {
        return content == null || content.isBlank();
    }
}
