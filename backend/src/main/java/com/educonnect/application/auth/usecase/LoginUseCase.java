package com.educonnect.application.auth.usecase;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.port.LoadUserPort;
import com.educonnect.application.auth.port.TokenPort;
import com.educonnect.domain.ApplicationUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application use case: authenticate user and return tokens + user info.
 */
@Service
public class LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;

    public LoginUseCase(LoadUserPort loadUserPort, TokenPort tokenPort, PasswordEncoder passwordEncoder) {
        this.loadUserPort = loadUserPort;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return LoginResponse or null if credentials invalid
     */
    public AuthDto.LoginResponse execute(String email, String password) {
        ApplicationUser user = loadUserPort.findByEmail(email).orElse(null);
        if (user == null || !user.isActive()) return null;
        if (!passwordEncoder.matches(password, user.getPasswordHash())) return null;

        List<String> roles = user.getRoles().stream().sorted().collect(Collectors.toList());
        String accessToken = tokenPort.createAccessToken(user.getId(), user.getEmail(), roles);
        String refreshToken = tokenPort.createRefreshToken(user.getId());

        AuthDto.LoginResponse response = new AuthDto.LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(tokenPort.getAccessTokenExpirationSeconds());
        response.setUser(toUserResponse(user, roles));
        return response;
    }

    private static AuthDto.UserResponse toUserResponse(ApplicationUser user, List<String> roles) {
        AuthDto.UserResponse u = new AuthDto.UserResponse();
        u.setId(user.getId());
        u.setEmail(user.getEmail());
        u.setFullName(user.getFullName());
        u.setRoles(roles);
        u.setMustChangePassword(user.isMustChangePassword());
        return u;
    }
}
