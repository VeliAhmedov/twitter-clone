package com.twittvl.backend.comment;

import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record CommentRequest(
        @Size(max = 150)
        String content,
        @URL
        String url
) {
}
