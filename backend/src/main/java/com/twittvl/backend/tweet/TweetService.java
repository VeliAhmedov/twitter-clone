package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.common.util.ServiceHelper;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final TweetLikeRepository tweetLikeRepository;
    private final CommentRepository commentRepository;
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, TweetMapper tweetMapper, TweetLikeRepository tweetLikeRepository, CommentRepository commentRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.tweetMapper = tweetMapper;
        this.tweetLikeRepository = tweetLikeRepository;
        this.commentRepository = commentRepository;
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
    // POST/api/tweets -> save new tweet to DB -> @CacheEvict -> DELETE all "feed" cache entries
    //reason is simple when new tweet posted cached feed isn't same because there are new posts that is why old feed is evicted
    public TweetResponse postTweet(Long userId, TweetRequest tweetRequest) {
        if(ServiceHelper.isBlank(tweetRequest.content()) && ServiceHelper.isBlank(tweetRequest.image())){
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + userId));
        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setContent(tweetRequest.content());
        tweet.setImageUrl(ServiceHelper.isBlank(tweetRequest.image()) ? null : tweetRequest.image());

        Tweet saved =  tweetRepository.save(tweet);
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
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId,pageable)
                .map(this::toTweetResponseWithCounts);
    }

    //getting global tweet feed
    @Transactional(readOnly = true)
    @Cacheable(value = "feed", key = "#pageable.pageNumber + '-' + #pageable.pageSize") //cache for getting feed
    // Request -> Redis cache? -> MISS -> Database -> get tweets -> save result in Redis (0-20 per page) -> return response
    public List<TweetResponse> getFeed(Pageable pageable) {
        return tweetRepository.findAllByOrderByCreatedAtDesc(pageable)
                .stream()
                .map(this::toTweetResponseWithCounts)
                .toList();
    }
    //ok return type of Page replaced by List because Redis serialization issue with Page, while it is a bit inconsistent,
    // trade of compared other options is low,

    //edit tweet
    @Transactional
    @CacheEvict(value = "feed", allEntries = true)
    public TweetResponse editTweet(Long id, Long userId, TweetRequest tweetRequest) {
        Tweet tweet = getOwnedTweet(id, userId, "modify");
        boolean changed = !Objects.equals(tweet.getContent(), tweetRequest.content()) ||
                !Objects.equals(tweet.getImageUrl(), tweetRequest.image());
        tweetMapper.applyUpdate(tweetRequest,tweet);
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

    //helper method
    private Tweet getOwnedTweet(Long tweetId, Long userId, String action) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + tweetId));
        if(!tweet.getUser().getId().equals(userId)){
            throw new AccessDeniedException("you can only " + action + " your own tweet");
        }
        return tweet;
    }
}
//cache system is a bit complicated as new tweet are posted, as feed is cleaned for every post, it might decrease benefit of cache