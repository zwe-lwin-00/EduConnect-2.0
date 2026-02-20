package com.educonnect.application.auth.usecase;

import com.educonnect.application.auth.dto.AuthDto;
import com.educonnect.application.auth.port.TokenPort;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.domain.RefreshToken;
import com.educonnect.repository.RefreshTokenRepository;
import com.educonnect.security.JwtService;
import com.educonnect.shared.util.HashUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Refresh access token using refresh token; rotates refresh token (revoke old, issue new).
 */
@Service
public class RefreshUseCase {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtService jwtService;
    private final TokenPort tokenPort;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshUseCase(JwtService jwtService, TokenPort tokenPort, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.tokenPort = tokenPort;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Validates refresh token, revokes it, issues new access + refresh tokens.
     * @return new LoginResponse or null if refresh token invalid/expired/revoked
     */
    public AuthDto.LoginResponse execute(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) return null;
        Claims claims;
        try {
            claims = jwtService.parseToken(refreshTokenValue);
        } catch (ExpiredJwtException | SignatureException | IllegalArgumentException e) {
            return null;
        }
        if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class))) return null;

        String userId = claims.getSubject();
        String tokenHash = HashUtil.sha256Hex(refreshTokenValue);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(tokenHash).orElse(null);
        if (rt == null || rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            return null;
        }

        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        ApplicationUser user = rt.getUser();
        List<String> roles = user.getRoles().stream().sorted().collect(Collectors.toList());
        String accessToken = tokenPort.createAccessToken(user.getId(), user.getEmail(), roles);
        String newRefreshToken = tokenPort.createRefreshToken(user.getId());

        AuthDto.LoginResponse response = new AuthDto.LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
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
