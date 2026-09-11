//package com.twittvl.backend.comment;
//
//import com.twittvl.backend.common.exception.ResourceNotFoundException;
//import com.twittvl.backend.tweet.Tweet;
//import com.twittvl.backend.tweet.TweetRepository;
//import com.twittvl.backend.user.User;
//import com.twittvl.backend.user.UserRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class CommentService {
//    private final CommentRepository commentRepository;
//    private final TweetRepository tweetRepository;
//    private final UserRepository userRepository;
//    public CommentService(CommentRepository commentRepository, TweetRepository tweetRepository, UserRepository userRepository) {
//        this.commentRepository = commentRepository;
//        this.tweetRepository = tweetRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Transactional()
//    public CommentResponse createComment(CommentRequest commentRequest, Long userId, Long tweetId) {
//        if (isBlank(commentRequest.content()) && isBlank(commentRequest.url())){
//            throw new IllegalArgumentException("comment can't be empty");
//        }
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("user not found with id " + userId));
//        Tweet tweet = tweetRepository.findById(tweetId)
//                .orElseThrow(() -> new ResourceNotFoundException("tweet not found with id " + tweetId));
//        Comment comment = new Comment();
//        comment.setTweet(tweet);
//        comment.setUser(user);
//        comment.setContent(commentRequest.content());
//        comment.setImageUrl(commentRequest.url());
//
//        Comment savedComment = commentRepository.save(comment);
//        return
//
//    }
//
//    private boolean isBlank(String content) {
//        return content == null || content.isBlank();
//    }
//}
