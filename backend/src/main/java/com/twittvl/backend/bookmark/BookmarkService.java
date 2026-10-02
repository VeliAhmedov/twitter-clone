package com.twittvl.backend.bookmark;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final BookmarkRepository bookmarkRepository;

    public BookmarkService(UserRepository userRepository, CommentRepository commentRepository,
                           TweetRepository tweetRepository, BookmarkRepository bookmarkRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @Transactional
    public long bookmarkTweet (Long userId, Long tweetId) {
        if (bookmarkRepository.existByUserIdAndTweetId(userId, tweetId)) {
            throw new ResourceNotFoundException("Tweet already bookmarked");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + userId + " not found"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with " + tweetId + " not found"));

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setTweet(tweet);
        bookmarkRepository.save(bookmark);

        return bookmarkRepository.countByTweetId(tweetId);
    }

    @Transactional
    public long bookmarkComment(Long userId, Long commentId) {
        if (bookmarkRepository.existByUserIdAndTweetId(userId, commentId)) {
            throw new ResourceNotFoundException("Comment already bookmarked");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with " + userId + " not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with " + commentId + " not found"));
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setComment(comment);
        bookmarkRepository.save(bookmark);
        return bookmarkRepository.countByCommentId(commentId);
    }

    @Transactional
    public long unbookmarkTweet (Long userId, Long tweetId) {
        if (!bookmarkRepository.existByUserIdAndTweetId(userId, tweetId)) {
            throw new ResourceNotFoundException("tweet with " + tweetId + " not found");
        }
        bookmarkRepository.deleteByUserIdAndTweetId(userId, tweetId);
        return bookmarkRepository.countByTweetId(tweetId);
    }

    @Transactional
    public long unbookmarkComment(Long userId, Long commentId) {
        if (!bookmarkRepository.existByUserIdAndCommentId(userId, commentId)) {
            throw new ResourceNotFoundException("comment with " + commentId + " not found");
        }
        bookmarkRepository.deleteByUserIdAndCommentId(userId, commentId);
        return bookmarkRepository.countByCommentId(commentId);
    }

    public Page<>
}
