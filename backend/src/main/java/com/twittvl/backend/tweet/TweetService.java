package com.twittvl.backend.tweet;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
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
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.tweetMapper = tweetMapper;
    }

    //Temporary to replace user creation
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

    //getting single tweet
    @Transactional(readOnly = true)
    public TweetResponse getById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + id));
        return tweetMapper.tweetToTweetResponse(tweet);
    }

    //getting user's tweet
    @Transactional(readOnly = true)
    public Page<TweetResponse> getByUserId(Long userId, Pageable pageable) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId,pageable)
                .map(tweetMapper::tweetToTweetResponse);
    }

    //getting global tweet feed
    @Transactional(readOnly = true)
    public Page<TweetResponse> getFeed(Pageable pageable) {
        return tweetRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(tweetMapper::tweetToTweetResponse);
    }

    //edit tweet
    @Transactional
    public TweetResponse editTweet(Long id, Long userId, TweetRequest tweetRequest) {
        Tweet tweet = getOwnedTweet(id, userId);
        boolean changed = !Objects.equals(tweet.getContent(), tweetRequest.content()) ||
                !Objects.equals(tweet.getImageUrl(), tweetRequest.image());
        tweetMapper.applyUpdate(tweetRequest,tweet);
        if (changed) {
            tweet.setEdited(true);
        }
        return tweetMapper.tweetToTweetResponse(tweet);
    }

    //helper methods
    private boolean isBlank(String content) {
        return content == null || content.isBlank();
    }
    private Tweet getOwnedTweet(Long tweetId, Long userId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet not found" + tweetId));
        if(!tweet.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("you can only edit your own tweet");
        }
        return tweet;
    }
}
