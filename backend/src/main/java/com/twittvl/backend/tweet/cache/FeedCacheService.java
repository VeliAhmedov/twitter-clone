package com.twittvl.backend.tweet.cache;

import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.retweet.RetweetRepository;
import com.twittvl.backend.tweet.*;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedCacheService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final TweetLikeRepository tweetLikeRepository;
    private final CommentRepository commentRepository;
    private final RetweetRepository retweetRepository;

    public FeedCacheService(
            TweetRepository tweetRepository, TweetMapper tweetMapper,
            TweetLikeRepository tweetLikeRepository, CommentRepository commentRepository,
            RetweetRepository retweetRepository) {

        this.tweetRepository = tweetRepository;
        this.tweetMapper = tweetMapper;
        this.tweetLikeRepository = tweetLikeRepository;
        this.commentRepository = commentRepository;
        this.retweetRepository = retweetRepository;
    }

    // feedCache service retrieves a page of tweets,
    // calculates likes/comments/retweets efficiently from queries in their repos,
    // converts the tweets into response objects, and caches the result in Redis
    @Transactional(readOnly = true)
    //check cache before executing method (retrieve from cache if exists otherwise retrieve from DB)
    @Cacheable(value = "feed", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public FeedCache getFeed(Pageable pageable) {
        Page<Tweet> page = tweetRepository.findAllByOrderByCreatedAtDesc(pageable); //retrieve tweets by create date

        List<Tweet> tweets = page.getContent(); //retrieve tweets of requested page (1-20, 21-40 and etc)

        //handling empty page, return empty feed cache with page info
        if (tweets.isEmpty()) {
            return new FeedCache(
                    List.of(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()
            );
        }
        //retrieve IDs of tweets of current page for using then in below operation
        List<Long> tweetIds = tweets.stream()
                .map(Tweet::getId)
                .toList();

        //from IDs of tweets we get from above, we use them to find count of fields from each one together
        //TweetId = 1, likes =6, comments = 7, retweets =8. each sorted to map ID -> count
        Map<Long, Long> likes = toCountMap(tweetLikeRepository.countLikesByTweetIds(tweetIds));
        Map<Long, Long> comments = toCountMap(commentRepository.countTopLevelCommentsByTweetIds(tweetIds));
        Map<Long, Long> retweets = toCountMap(retweetRepository.countRetweetsByTweetIds(tweetIds));

        //accumulate to response (content) with all count together alongside, getOrDefault used for potential 0 value
        //all process batched with less mass query operation, to prevent massive N+1 ops
        List<TweetResponse> content = tweets.stream()
                .map(tweet -> tweetMapper.tweetToTweetResponse(tweet)
                        .withCounts(
                                likes.getOrDefault(tweet.getId(), 0L),
                                comments.getOrDefault(tweet.getId(), 0L),
                                retweets.getOrDefault(tweet.getId(), 0L)
                        ))
                .toList();

        //after result return whole value with page data for caching
        return new FeedCache(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    //convert need results into map with projection
    private Map<Long, Long> toCountMap(List<TweetCountProjection> projections) {
        return projections.stream()
                .collect(Collectors.toMap(
                        TweetCountProjection::getTweetId,
                        TweetCountProjection::getCountValue
                ));
    }
}