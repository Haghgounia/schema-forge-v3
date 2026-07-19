package com.behsazan.schemaforge.api.common;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        String correlationId,
        Instant timestamp) {

    public static <T> ApiResponse<T> success(T data, String correlationId) {
        return new ApiResponse<>(true, data, correlationId, Instant.now());
    }
}
