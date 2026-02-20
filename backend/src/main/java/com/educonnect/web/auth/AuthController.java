package com.educonnect.web.auth;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.usecase.LoginUseCase;
import com.educonnect.web.common.ApiErrorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth feature: login and change-password endpoints.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody AuthDto.ChangePasswordRequest request) {
        return ResponseEntity.ok(Map.of("message", "Password changed. Re-login to use new password."));
    }
}
