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

    //this does put response alongside updated like count
    public TweetResponse toResponse(Tweet tweet) {
        //how many likes does tweet have
        long likesCount = tweetLikeRepository.countByTweetId(tweet.getId());
        //how many comments does tweet have
        long commentCount = commentRepository.countByTweetIdAndParentCommentIsNull(tweet.getId());
        //map entity then swap default 0 like with real number
        return tweetMapper.tweetToTweetResponse(tweet).withCounts(likesCount, commentCount);
    }
}
