package com.swarnlink.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String error,
        int status,
        LocalDateTime timestamp,
        String path
) {
}