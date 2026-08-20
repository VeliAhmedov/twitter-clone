package com.twittvl.backend.tweet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

public record TweetRequest(
        @Size(min = 1, max = 280)
        String content,
        @URL
        String image
) {
}
