package com.educonnect.web.auth;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.usecase.GetCurrentUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth feature: current user (me) endpoint.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthMeController {

    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null || "anonymousUser".equals(userId)) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        AuthDto.UserResponse user = getCurrentUserUseCase.execute(userId);
        if (user == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(user);
    }
}
