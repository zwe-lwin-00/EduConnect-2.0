package com.educonnect.web.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Consistent API error body for exception handling middleware.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    /** Short error type (e.g. "Validation failed"). */
    private String error;
    /** Machine-readable code (e.g. VALIDATION_FAILED, UNAUTHORIZED). */
    private String code;
    /** Human-readable message. */
    private String message;
    private Integer status;
    private List<String> details;
    private String path;
    /** Request correlation ID for tracing (from MDC). */
    private String requestId;

    public static ApiErrorResponse of(String error, String code, String message, int status) {
        return ApiErrorResponse.builder()
                .error(error)
                .code(code)
                .message(message)
                .status(status)
                .build();
    }
}
