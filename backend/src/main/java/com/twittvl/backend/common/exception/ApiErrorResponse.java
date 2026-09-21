package com.twittvl.backend.common.exception;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String message,
        String error,
        String path,
        Instant timestamp
) {
}
