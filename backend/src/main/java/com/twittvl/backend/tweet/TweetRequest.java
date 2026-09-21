package com.twittvl.backend.tweet;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record TweetRequest(
        @Size(min = 1, max = 280)
        String content,
        @URL
        String image
) {
}
