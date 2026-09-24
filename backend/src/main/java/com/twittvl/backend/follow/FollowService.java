package com.twittvl.backend.follow;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.notification.NotificationProducer;
import com.twittvl.backend.notification.NotificationType;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;
    private final NotificationProducer notificationProducer;

    public FollowService(FollowRepository followRepository, UserRepository userRepository, FollowMapper followMapper, NotificationProducer notificationProducer) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.followMapper = followMapper;
        this.notificationProducer = notificationProducer;
    }

    //follow user, return long to increased user amount when unfollowed
    @Transactional
    public long followUser(Long followerId, Long followedId) {
        if (followerId.equals(followedId)) throw new IllegalArgumentException("you can't follow yourself");
        if (followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new IllegalArgumentException("you are already following");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("user with " + followerId + " not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new ResourceNotFoundException("user with " + followedId + " not found"));
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        followRepository.save(follow);
        //send notifications
        notificationProducer.sendNotification(followerId, followedId, NotificationType.FOLLOW, null, null);
        return followRepository.countByFollowedId(followedId);
    }

    //unfollow user, return long to decreased user amount when unfollower
    @Transactional
    public long unfollowUser(Long followerId, Long followedId) {
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
                .orElseThrow(() -> new ResourceNotFoundException("follow relationship not found"));
        followRepository.delete(follow);
        return followRepository.countByFollowedId(followedId);
    }

    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findAllByFollowedIdOrderByCreatedAtDesc(userId, pageable)
                .map(follow -> followMapper.userToFollowUserResponse(follow.getFollower()));
    }

    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findAllByFollowerIdOrderByCreatedAtDesc(userId, pageable)
                .map(follow -> followMapper.userToFollowUserResponse(follow.getFollowed()));
    }
    @Transactional(readOnly = true)
    public FollowStatsResponse getFollowStats(Long userId) {
        long followerCount = followRepository.countByFollowedId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        return new FollowStatsResponse(followerCount, followingCount);
    }
}
