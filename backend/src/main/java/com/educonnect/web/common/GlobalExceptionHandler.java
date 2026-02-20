package com.educonnect.web.common;

import com.educonnect.shared.logging.LoggerExtensions;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception handling middleware: consistent API error responses (error, code, details?, requestId?).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String REQUEST_ID_MDC = "requestId";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());
        ApiErrorResponse body = ApiErrorResponse.builder()
                .error("Validation failed")
                .code("VALIDATION_FAILED")
                .message("Invalid request body")
                .status(HttpStatus.BAD_REQUEST.value())
                .details(details)
                .path(request.getRequestURI())
                .requestId(MDC.get(REQUEST_ID_MDC))
                .build();
        LoggerExtensions.warn(log, "Validation failed path={} details={}", request.getRequestURI(), details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            IllegalArgumentException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(
                "Bad request",
                "BAD_REQUEST",
                ex.getMessage() != null ? ex.getMessage() : "Bad request",
                HttpStatus.BAD_REQUEST.value());
        body.setPath(request.getRequestURI());
        body.setRequestId(MDC.get(REQUEST_ID_MDC));
        LoggerExtensions.warn(log, "Bad request path={} message={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            IllegalStateException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(
                "Conflict",
                "CONFLICT",
                ex.getMessage() != null ? ex.getMessage() : "Conflict",
                HttpStatus.CONFLICT.value());
        body.setPath(request.getRequestURI());
        body.setRequestId(MDC.get(REQUEST_ID_MDC));
        LoggerExtensions.warn(log, "Conflict path={} message={}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            AccessDeniedException ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(
                "Forbidden",
                "FORBIDDEN",
                ex.getMessage() != null ? ex.getMessage() : "Access denied",
                HttpStatus.FORBIDDEN.value());
        body.setPath(request.getRequestURI());
        body.setRequestId(MDC.get(REQUEST_ID_MDC));
        LoggerExtensions.warn(log, "Forbidden path={}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.of(
                "Internal server error",
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.setPath(request.getRequestURI());
        body.setRequestId(MDC.get(REQUEST_ID_MDC));
        LoggerExtensions.error(log, "Unhandled exception path=" + request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
