package com.educonnect.application.auth.usecase;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.port.LoadUserPort;
import com.educonnect.domain.ApplicationUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application use case: get current user by id (from security context).
 */
@Service
public class GetCurrentUserUseCase {

    private final LoadUserPort loadUserPort;

    public GetCurrentUserUseCase(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    public AuthDto.UserResponse execute(String userId) {
        return loadUserPort.findById(userId)
                .map(this::toUserResponse)
                .orElse(null);
    }

    private AuthDto.UserResponse toUserResponse(ApplicationUser user) {
        List<String> roles = user.getRoles().stream().sorted().collect(Collectors.toList());
        return new AuthDto.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roles,
                user.isMustChangePassword()
        );
    }
}
