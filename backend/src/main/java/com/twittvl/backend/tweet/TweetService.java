package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.common.util.ServiceHelper;
import com.twittvl.backend.notification.NotificationProducer;
import com.twittvl.backend.notification.NotificationType;
import com.twittvl.backend.tweet.cache.FeedCache;
import com.twittvl.backend.tweet.cache.FeedCacheService;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
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
    private final TweetLikeRepository tweetLikeRepository;
    private final CommentRepository commentRepository;
    private final FeedCacheService feedCacheService;
    private final NotificationProducer notificationProducer;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository,
                        TweetMapper tweetMapper, TweetLikeRepository tweetLikeRepository,
                        CommentRepository commentRepository, FeedCacheService feedCacheService,
                        NotificationProducer notificationProducer) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.tweetMapper = tweetMapper;
        this.tweetLikeRepository = tweetLikeRepository;
        this.commentRepository = commentRepository;
        this.feedCacheService = feedCacheService;
        this.notificationProducer = notificationProducer;
    }

    //this does put response alongside updated like count
    private TweetResponse toTweetResponseWithCounts(Tweet tweet) {
        //how many likes does comment have
        long likeCount = tweetLikeRepository.countByTweetId(tweet.getId());
        //how many comments does tweet have
        long commentCount = commentRepository.countByTweetIdAndParentCommentIsNull(tweet.getId());
        //map entity then swap default 0 like with real number
        return tweetMapper.tweetToTweetResponse(tweet).withCounts(likeCount, commentCount);
    }

    //Temporary to replace user creation
    @Transactional
    @CacheEvict(value = "feed", allEntries = true)
    public TweetResponse postTweet(Long userId, TweetRequest tweetRequest) {
        if (ServiceHelper.isBlank(tweetRequest.content()) && ServiceHelper.isBlank(tweetRequest.image())) {
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }

        //first check if tweet is quoted or not
        Tweet quoted = null;
        if (tweetRequest.quotedTweetId() != null) {
            quoted = tweetRepository.findById(tweetRequest.quotedTweetId())
                    .orElseThrow(() -> new ResourceNotFoundException
                            ("Quoted tweet with " + tweetRequest.quotedTweetId() + " not found"));
        }

        Tweet tweet = new Tweet();
        tweet.setUser(userRepository.getReferenceById(userId));
        tweet.setContent(tweetRequest.content());
        tweet.setImageUrl(ServiceHelper.isBlank(tweetRequest.image()) ? null : tweetRequest.image());
        tweet.setQuotedTweet(quoted); //set value if tweet is quoted if not null

        Tweet saved = tweetRepository.save(tweet);
        //send notification to one who you quoted his/her tweet
        if (quoted != null) {
            notificationProducer.sendNotification(
                    userId,
                    quoted.getUser().getId(),
                    NotificationType.QUOTE,
                    saved.getId(),
                    null
            );
        }

        return toTweetResponseWithCounts(saved);
    }

    //getting single tweet
    @Transactional(readOnly = true)
    public TweetResponse getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + id));
        return toTweetResponseWithCounts(tweet);
    }

    //getting user's tweet
    @Transactional(readOnly = true)
    public Page<TweetResponse> getByUserId(Long userId, Pageable pageable) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toTweetResponseWithCounts);
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
        boolean changed = !Objects.equals(tweet.getContent(), tweetRequest.content()) ||
                !Objects.equals(tweet.getImageUrl(), tweetRequest.image());
        tweetMapper.applyUpdate(tweetRequest, tweet);
        if (changed) {
            tweet.setEdited(true);
        }
        return toTweetResponseWithCounts(tweet);
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
    public Page<TweetResponse> getQuotes(Long tweetId, Pageable pageable) {
        return tweetRepository.findByQuotedTweetIdOrderByCreatedAtDesc(tweetId, pageable)
                .map(this::toTweetResponseWithCounts);
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
