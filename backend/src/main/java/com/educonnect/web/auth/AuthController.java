package com.educonnect.web.auth;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.usecase.LoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody AuthDto.ChangePasswordRequest request) {
        return ResponseEntity.ok(Map.of("message", "Password changed. Re-login to use new password."));
    }
}
