package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.common.util.ServiceHelper;
import com.twittvl.backend.notification.NotificationProducer;
import com.twittvl.backend.notification.NotificationType;
import com.twittvl.backend.tweet.cache.FeedCache;
import com.twittvl.backend.tweet.cache.FeedCacheService;
import com.twittvl.backend.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final CommentRepository commentRepository;
    private final FeedCacheService feedCacheService;
    private final NotificationProducer notificationProducer;
    private final TweetResponseAssembler tweetResponseAssembler;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository,
                        TweetMapper tweetMapper, CommentRepository commentRepository,
                        FeedCacheService feedCacheService, NotificationProducer notificationProducer,
                        TweetResponseAssembler tweetResponseAssembler) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.tweetMapper = tweetMapper;
        this.commentRepository = commentRepository;
        this.feedCacheService = feedCacheService;
        this.notificationProducer = notificationProducer;
        this.tweetResponseAssembler = tweetResponseAssembler;
    }

    //Temporary to replace user creation
    @Transactional
    @CacheEvict(value = "feed", allEntries = true)
    public TweetResponse postTweet(Long userId, TweetRequest tweetRequest) {
        if (ServiceHelper.isBlank(tweetRequest.content()) && ServiceHelper.isBlank(tweetRequest.image())) {
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }

        if (tweetRequest.quotedTweetId() != null && tweetRequest.quotedCommentId() != null) {
            throw new IllegalArgumentException("you can either quote tweet or comment");
        }

        //first check if tweet or comment is quoted or not
        Tweet quotedTweet = null;
        Comment quotedComment = null;
        if (tweetRequest.quotedTweetId() != null) {
            quotedTweet = tweetRepository.findById(tweetRequest.quotedTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("Quoted tweet with " + tweetRequest.quotedTweetId() + " not found"));
        }else if (tweetRequest.quotedCommentId() != null) {
            quotedComment = commentRepository.findById(tweetRequest.quotedCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("Quoted comment with " + tweetRequest.quotedCommentId() + " not found"));
        }

        Tweet tweet = new Tweet();
        tweet.setUser(userRepository.getReferenceById(userId));
        tweet.setContent(tweetRequest.content());
        tweet.setImageUrl(ServiceHelper.isBlank(tweetRequest.image()) ? null : tweetRequest.image());
        tweet.setQuotedTweet(quotedTweet); //set value if tweet is quoted if not null
        tweet.setQuotedComment(quotedComment);


        Tweet saved = tweetRepository.save(tweet);

        //send notification to one who you quoted his/her tweet or comment
        if (quotedTweet != null) {
            notificationProducer.sendNotification(
                    userId,
                    quotedTweet.getUser().getId(),
                    NotificationType.QUOTE_TWEET,
                    saved.getId(),
                    null
            );
        }else if (quotedComment != null) {
            notificationProducer.sendNotification(
                    userId,
                    quotedComment.getUser().getId(),
                    NotificationType.QUOTE_COMMENT,
                    saved.getId(),
                    quotedComment.getId()
            );
        }
        //mark this post if either of it available, though it is obvious this is for better showing unavailable quoted tweet
        tweet.setQuote(quotedTweet != null || quotedComment != null);

        return tweetResponseAssembler.toResponse(saved);
    }

    //getting single tweet
    @Transactional(readOnly = true)
    public TweetResponse getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + id));
        return tweetResponseAssembler.toResponse(tweet);
    }

    //getting user's tweet
    @Transactional(readOnly = true)
    public Page<TweetResponse> getByUserId(Long userId, Pageable pageable) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(tweetResponseAssembler::toResponse);
    }

    //getting global tweet feed
    @Transactional(readOnly = true)
    public Page<TweetResponse> getFeed(Pageable pageable) {
        FeedCache cachedFeed = feedCacheService.getFeed(pageable);
        return new PageImpl<>(cachedFeed.content(), pageable, cachedFeed.totalElements());
    }

    //edit tweet
    @Transactional
    @CacheEvict(value = "feed", allEntries = true)
    public TweetResponse editTweet(Long id, Long userId, TweetRequest tweetRequest) {
        Tweet tweet = getOwnedTweet(id, userId, "modify");
        boolean changed =
                (tweetRequest.content() != null && !Objects.equals(tweet.getContent(), tweetRequest.content())) ||
                (tweetRequest.image() != null && !Objects.equals(tweet.getImageUrl(), tweetRequest.image()));
        tweetMapper.applyUpdate(tweetRequest, tweet);
        if (changed) {
            tweet.setEdited(true);
        }
        return tweetResponseAssembler.toResponse(tweet);
    }

    //hard deletes tweet
    @Transactional
    @CacheEvict(value = "feed", allEntries = true)
    public void deleteTweet(Long id, Long userId) {
        Tweet tweet = getOwnedTweet(id, userId, "delete");
        tweetRepository.delete(tweet);
    }

    //get all quoted tweets of tweet
    @Transactional(readOnly = true)
    public Page<TweetResponse> getQuotesByTweetId(Long tweetId, Pageable pageable) {
        return tweetRepository.findByQuotedTweetIdOrderByCreatedAtDesc(tweetId, pageable)
                .map(tweetResponseAssembler::toResponse);
    }

    //get all quoted tweets of comment
    @Transactional(readOnly = true)
    public Page<TweetResponse> getQuotedByCommentId(Long commentId, Pageable pageable) {
        return tweetRepository.findByQuotedCommentIdOrderByCreatedAtDesc(commentId, pageable)
                .map(tweetResponseAssembler::toResponse);
    }

    //helper method
    private Tweet getOwnedTweet(Long tweetId, Long userId, String action) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + tweetId));
        if (!tweet.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("you can only " + action + " your own tweet");
        }
        return tweet;
    }
}
