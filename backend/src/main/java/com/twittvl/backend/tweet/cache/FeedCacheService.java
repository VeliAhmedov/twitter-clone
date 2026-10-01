package com.twittvl.backend.tweet.cache;

import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetMapper;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.tweet.TweetResponse;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedCacheService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final TweetLikeRepository tweetLikeRepository;
    private final CommentRepository commentRepository;

    public FeedCacheService(
            TweetRepository tweetRepository,
            TweetMapper tweetMapper,
            TweetLikeRepository tweetLikeRepository,
            CommentRepository commentRepository) {

        this.tweetRepository = tweetRepository;
        this.tweetMapper = tweetMapper;
        this.tweetLikeRepository = tweetLikeRepository;
        this.commentRepository = commentRepository;
    }

    //we put to
    @Transactional(readOnly = true)
    @Cacheable(value = "feed", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public FeedCache getFeed(Pageable pageable) {
        Page<Tweet> page = tweetRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<TweetResponse> content = page.getContent()
                .stream()
                .map(this::toTweetResponseWithCounts)
                .toList();

        return new FeedCache(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private TweetResponse toTweetResponseWithCounts(Tweet tweet) {
        long likeCount = tweetLikeRepository.countByTweetId(tweet.getId());
        long commentCount = commentRepository.countByTweetIdAndParentCommentIsNull(tweet.getId());
        return tweetMapper.tweetToTweetResponse(tweet).withCounts(likeCount, commentCount);
    }
}