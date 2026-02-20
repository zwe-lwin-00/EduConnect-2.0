package com.educonnect.web.auth;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.usecase.ChangePasswordUseCase;
import com.educonnect.application.auth.usecase.LoginUseCase;
import com.educonnect.application.auth.usecase.RefreshUseCase;
import com.educonnect.application.auth.usecase.RevokeRefreshTokensUseCase;
import com.educonnect.web.common.ApiErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Auth feature: login, refresh, logout, change-password.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;
    private final RevokeRefreshTokensUseCase revokeRefreshTokensUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.LoginResponse response = loginUseCase.execute(request.getEmail(), request.getPassword());
        if (response == null) {
            ApiErrorResponse body = ApiErrorResponse.of(
                    "Unauthorized",
                    "INVALID_CREDENTIALS",
                    "Invalid credentials",
                    401);
            body.setRequestId(MDC.get("requestId"));
            return ResponseEntity.status(401).body(body);
        }
        return ResponseEntity.ok(response);
    }

    /** Exchange refresh token for new access + refresh tokens (rotation: old refresh token is revoked). */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body != null ? body.get("refreshToken") : null;
        AuthDto.LoginResponse response = refreshUseCase.execute(refreshToken);
        if (response == null) {
            ApiErrorResponse apiError = ApiErrorResponse.of(
                    "Unauthorized",
                    "INVALID_REFRESH_TOKEN",
                    "Invalid or expired refresh token",
                    401);
            apiError.setRequestId(MDC.get("requestId"));
            return ResponseEntity.status(401).body(apiError);
        }
        return ResponseEntity.ok(response);
    }

    /** Revoke all refresh tokens for the current user. Call when user logs out. */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            String userId = auth.getPrincipal().toString();
            revokeRefreshTokensUseCase.execute(userId);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    /** Requires authentication. Changes password for the current user. */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody AuthDto.ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            ApiErrorResponse body = ApiErrorResponse.of("Unauthorized", "NOT_AUTHENTICATED", "Authentication required", 401);
            body.setRequestId(MDC.get("requestId"));
            return ResponseEntity.status(401).body(body);
        }
        String userId = (String) auth.getPrincipal();
        boolean changed = changePasswordUseCase.execute(userId, request.getCurrentPassword(), request.getNewPassword());
        if (!changed) {
            ApiErrorResponse body = ApiErrorResponse.of("Bad Request", "INVALID_CURRENT_PASSWORD", "Current password is incorrect", 400);
            body.setRequestId(MDC.get("requestId"));
            return ResponseEntity.status(400).body(body);
        }
        return ResponseEntity.ok(Map.of("message", "Password changed. Re-login to use new password."));
    }
}
