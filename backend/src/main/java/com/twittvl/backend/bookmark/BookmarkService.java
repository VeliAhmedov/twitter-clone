package com.twittvl.backend.bookmark;

import com.twittvl.backend.comment.Comment;
import com.twittvl.backend.comment.CommentRepository;
import com.twittvl.backend.common.exception.ConflictException;
import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.tweet.Tweet;
import com.twittvl.backend.tweet.TweetRepository;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookmarkService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    public BookmarkService(UserRepository userRepository, CommentRepository commentRepository,
                           TweetRepository tweetRepository, BookmarkRepository bookmarkRepository,
                           BookmarkMapper bookmarkMapper) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.bookmarkMapper = bookmarkMapper;
    }

    //to see count of bookmarks removed because for user, it is better
    @Transactional
    public void bookmarkTweet(Long userId, Long tweetId) {
        if (bookmarkRepository.existsByUserIdAndTweetId(userId, tweetId)) {
            throw new ConflictException("Tweet already bookmarked");
        }

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with " + tweetId + " not found"));

        //replaced get and check user explicitly, authPrinciple in controller guarantees user
        //getReference replaces findById, it returns lazy proxy to hit DB which is enough
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(userRepository.getReferenceById(userId));
        bookmark.setTweet(tweet);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void bookmarkComment(Long userId, Long commentId) {
        if (bookmarkRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new ConflictException("Comment already bookmarked");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with " + commentId + " not found"));
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(userRepository.getReferenceById(userId));
        bookmark.setComment(comment);
        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void unbookmarkTweet(Long userId, Long tweetId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndTweetId(userId, tweetId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark with not found"));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional
    public void unbookmarkComment(Long userId, Long commentId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark with not found"));

        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getBookmarksByUserId(Long userId, Pageable pageable) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(bookmarkMapper::toBookmarkResponse);
    }
}
