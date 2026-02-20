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
    private String error;
    private String message;
    private Integer status;
    private List<String> details;
    private String path;

    public static ApiErrorResponse of(String error, String message, int status) {
        return ApiErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status)
                .build();
    }
}
