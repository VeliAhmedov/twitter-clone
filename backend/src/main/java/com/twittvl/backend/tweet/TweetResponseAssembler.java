package com.twittvl.backend.tweet;

import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.tweetLike.TweetLikeRepository;
import org.springframework.stereotype.Component;

@Component
public class TweetResponseAssembler {
    private final TweetMapper tweetMapper;
    private final TweetLikeRepository tweetLikeRepository;
    private final CommentRepository commentRepository;

    public TweetResponseAssembler(TweetMapper tweetMapper, TweetLikeRepository tweetLikeRepository,
                                  CommentRepository commentRepository) {
        this.tweetMapper = tweetMapper;
        this.tweetLikeRepository = tweetLikeRepository;
        this.commentRepository = commentRepository;
    }

    public TweetResponse assembleResponse(Tweet tweet) {
        long likesCount = tweetLikeRepository.countByTweetId(tweet.getId());
        long commentCount = commentRepository.countByTweetIdAndParentCommentIsNull(tweet.getId());
        return tweetMapper.tweetToTweetResponse(tweet).withCounts(likesCount, commentCount);
    }
}
