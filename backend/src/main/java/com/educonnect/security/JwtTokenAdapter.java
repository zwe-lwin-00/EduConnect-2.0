package com.educonnect.security;

import com.educonnect.application.auth.port.TokenPort;
import com.educonnect.config.JwtProperties;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.domain.RefreshToken;
import com.educonnect.repository.ApplicationUserRepository;
import com.educonnect.repository.RefreshTokenRepository;
import com.educonnect.shared.util.HashUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * TokenPort implementation: creates access/refresh JWTs and stores refresh tokens hashed in DB.
 */
@Component
@Primary
public class JwtTokenAdapter implements TokenPort {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationUserRepository applicationUserRepository;

    public JwtTokenAdapter(JwtService jwtService,
                           JwtProperties jwtProperties,
                           RefreshTokenRepository refreshTokenRepository,
                           ApplicationUserRepository applicationUserRepository) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public String createAccessToken(String userId, String email, List<String> roles) {
        return jwtService.createAccessToken(userId, email, roles);
    }

    @Override
    public String createRefreshToken(String userId) {
        String rawToken = jwtService.createRefreshToken(userId);
        String tokenHash = HashUtil.sha256Hex(rawToken);
        ApplicationUser user = applicationUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs());
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
        return rawToken;
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return jwtService.getAccessTokenExpirationSeconds();
    }
}
