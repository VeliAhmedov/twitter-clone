package com.twittvl.backend.retweet;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.comment.CommentResponseAssembler;
import com.twittvl.backend.common.exception.ConflictException;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.notification.NotificationProducer;
import com.twittvl.backend.notification.NotificationType;
import com.twittvl.backend.tweet.*;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RetweetService {
    private final RetweetRepository retweetRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final CommentResponseAssembler commentResponseAssembler;
    private final TweetResponseAssembler tweetResponseAssembler;
    private final NotificationProducer notificationProducer;
    private final TweetViewerState tweetViewerState;

    public RetweetService(RetweetRepository retweetRepository, CommentRepository commentRepository,
                          UserRepository userRepository, TweetRepository tweetRepository,
                          CommentResponseAssembler commentResponseAssembler, TweetResponseAssembler tweetResponseAssembler,
                          NotificationProducer notificationProducer, TweetViewerState tweetViewerState) {
        this.retweetRepository = retweetRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
        this.commentResponseAssembler = commentResponseAssembler;
        this.tweetResponseAssembler = tweetResponseAssembler;
        this.notificationProducer = notificationProducer;
        this.tweetViewerState = tweetViewerState;
    }

    @Transactional
    public long retweetTweet(Long userId, Long tweetId) {
        if (retweetRepository.existsByUserIdAndTweetId(userId, tweetId)) {
            throw new ConflictException("Tweet already retweeted!");
        }
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id: " + tweetId + " not found!"));

        Retweet retweet = new Retweet();
        retweet.setTweet(tweet);
        retweet.setUser(userRepository.getReferenceById(userId));
        retweetRepository.save(retweet);

        notificationProducer.sendNotification(
                userId,
                tweet.getUser().getId(),
                NotificationType.RETWEET_TWEET,
                tweetId,
                null);

        return retweetRepository.countByTweetId(tweetId);
    }

    @Transactional
    public long retweetComment(Long userId, Long commentId) {
        if (retweetRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new ConflictException("Comment already retweeted!");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id: " + commentId + " not found!"));

        Retweet retweet = new Retweet();
        retweet.setComment(comment);
        retweet.setUser(userRepository.getReferenceById(userId));
        retweetRepository.save(retweet);

        notificationProducer.sendNotification(
                userId,
                comment.getUser().getId(),
                NotificationType.RETWEET_COMMENT,
                comment.getTweet().getId(),
                commentId);

        return retweetRepository.countByCommentId(commentId);
    }

    @Transactional
    public long unRetweetTweet(Long userId, Long tweetId) {
        Retweet retweet = retweetRepository.findByUserIdAndTweetId(userId, tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id: " + tweetId + " not found!"));
        retweetRepository.delete(retweet);
        return retweetRepository.countByTweetId(tweetId);
    }

    @Transactional
    public long unRetweetComment(Long userId, Long commentId) {
        Retweet retweet = retweetRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id: " + commentId + " not found!"));
        retweetRepository.delete(retweet);
        return retweetRepository.countByCommentId(commentId);
    }

    //because retweet is just reposted tweet, it needs view state to show filled in flags
    @Transactional(readOnly = true)
    public Page<RetweetResponse> getRetweetsByUserId(Long userId, Pageable pageable) {
        Page<Retweet> page = retweetRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        Map<Long, TweetResponse> tweets = tweetViewerState.apply(
                        page.getContent().stream().map(Retweet::getTweet).filter(Objects::nonNull)
                                .map(tweetResponseAssembler::toResponse).toList())
                .stream().collect(Collectors.toMap(TweetResponse::id, t -> t));

        return page.map(r -> new RetweetResponse(
                r.getId(), r.getCreatedAt(),
                r.getTweet() != null ? tweets.get(r.getTweet().getId()) : null,
                r.getComment() != null ? commentResponseAssembler.toResponse(r.getComment()) : null));
    }
}
//overall change is that private like/comment count methods will be converted to special assembler classes
//same functionality just in class which is better than turning private method to public and break encapsulation

//TweetService ─────────┐
//                      ↓
//              TweetResponseAssembler
//                      ↑
//RetweetService ───────┘
//
//CommentService ───────┐
//                      ↓
//            CommentResponseAssembler
//                      ↑
//RetweetService ───────┘