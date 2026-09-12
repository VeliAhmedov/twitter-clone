package com.twittvl.backend.follow;

import com.twittvl.backend.common.exception.ResourceNotFoundException;
import com.twittvl.backend.user.User;
import com.twittvl.backend.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    //follow user, return long to increased user amount when unfollowed
    @Transactional
    public long followUser(Long followedId, Long followerId) {
        if (followerId.equals(followedId)) throw new IllegalArgumentException("you can't follow yourself");
        if (!followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new IllegalArgumentException("you are already following");
        }
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("user with" + followerId + "not found"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new ResourceNotFoundException("user with" + followedId + "not found"));
        Follow follow = new Follow();
        follow.setFollowed(followed);
        follow.setFollower(follower);
        followRepository.save(follow);
        return followRepository.countByFollowedId(followedId);
    }

    //unfollow user, return long to decreased user amount when unfollowed
    @Transactional
    public long unfollowUser(Long followedId, Long followerId) {
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId)
                        .orElseThrow(() -> new ResourceNotFoundException("followedId " + followedId + " not found"));
        followRepository.delete(follow);
        return followRepository.countByFollowedId(followedId);
    }
}
