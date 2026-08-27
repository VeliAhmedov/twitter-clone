package com.twittvl.backend.tweet;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.tweetMapper = tweetMapper;
    }

    @Transactional
    public TweetResponse createTweet(Long userId, TweetRequest tweetRequest) {
        if(isBlank(tweetRequest.content()) && isBlank(tweetRequest.image())){
            throw new IllegalArgumentException("Tweet content cannot be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found" + userId));
        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setContent(tweetRequest.content());
        tweet.setImageUrl(tweetRequest.image());

        Tweet saved =  tweetRepository.save(tweet);
        return tweetMapper.tweetToTweetResponse(saved);
    }
    private boolean isBlank(String content) {
        return content == null || content.isBlank();
    }
}
