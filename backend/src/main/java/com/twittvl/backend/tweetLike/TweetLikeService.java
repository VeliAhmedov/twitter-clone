package com.twittvl.backend.tweetLike;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetMapper;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TweetLikeService {
    private final TweetLikeRepository tweetLikeRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    private final TweetLikeMapper tweetLikeMapper;
    public TweetLikeService(TweetLikeRepository tweetLikeRepository, UserRepository userRepository, TweetRepository tweetRepository,  TweetLikeMapper tweetLikeMapper) {
        this.tweetLikeRepository = tweetLikeRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
        this.tweetLikeMapper = tweetLikeMapper;
    }

    //like tweet, return long to increase amount when liked
    @Transactional
    public long likeTweet (Long userId, Long tweetId) {
        if (tweetLikeRepository.existsByTweetIdAndUserId(tweetId, userId)) {
            throw new IllegalArgumentException("Tweet with id " + tweetId + " is already liked");
        }
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + tweetId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        TweetLike like = new TweetLike();
        like.setUser(user);
        like.setTweet(tweet);
        tweetLikeRepository.save(like);
        return tweetLikeRepository.countByTweetId(tweetId);
    }

    //unlike tweet, return long to decrease amount when liked
    @Transactional
    public long unlikeTweet (Long userId, Long tweetId) {
        TweetLike like = tweetLikeRepository.findByTweetIdAndUserId(tweetId, userId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Like not found for tweet " + tweetId + " and user " + userId));
        tweetLikeRepository.delete(like);
        return tweetLikeRepository.countByTweetId(tweetId);
    }



}
