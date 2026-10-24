package com.twittvl.backend.tweet;

public interface TweetCountProjection {
    Long getTweetId();

    Long getCountValue();
}

//projection interface will allow us to take only those 2 columns for batch operations without need of mapper

//unlike DTO which takes whole row and process needed value via mapper

//Projection interface and DTO record do same operations with serving different purposes