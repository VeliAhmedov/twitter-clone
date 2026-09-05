package com.twittvl.backend.like;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import com.twittvl.backend.user.UserService;
import org.mapstruct.control.MappingControl;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;
    public LikeService(LikeRepository likeRepository, UserRepository userRepository, TweetRepository tweetRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    public long likeTweet (Long userId, Long tweetId) {
        if (!likeRepository.existsByTweetIdAndUserId(tweetId, userId)) {
            throw new IllegalArgumentException("Tweet with id " + tweetId + " is already liked");
        }
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + tweetId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);
        likeRepository.save(like);
        return likeRepository.countByTweetId(tweetId);
    }
    public long unlikeTweet (Long userId, Long tweetId) {
        if (!likeRepository.existsByTweetIdAndUserId(tweetId, userId)) {
            throw new IllegalArgumentException("Tweet with id " + tweetId + " is already unliked");
        }
        likeRepository.deleteById(tweetId);
        return likeRepository.countByTweetId(tweetId);
    }

}
