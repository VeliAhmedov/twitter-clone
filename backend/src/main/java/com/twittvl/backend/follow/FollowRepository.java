package com.twittvl.backend.follow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowedId(Long followerId, Long followedId);

    boolean existsByFollowerIdAndFollowedId(Long followerId, Long followedId);

    long countByFollowedId(Long followedId); // number of followers

    long countByFollowerId(Long followerId); // number of people this user follows

    Page<Follow> findAllByFollowedIdOrderByCreatedAtDesc(Long followedId, Pageable pageable);
    Page<Follow> findAllByFollowerIdOrderByCreatedAtDesc(Long followerId, Pageable pageable);
}
