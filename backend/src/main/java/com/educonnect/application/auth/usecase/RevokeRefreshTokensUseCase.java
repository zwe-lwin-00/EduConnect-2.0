package com.educonnect.application.auth.usecase;

import com.educonnect.domain.RefreshToken;
import com.educonnect.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Revokes all refresh tokens for a user (e.g. on logout). */
@Service
public class RevokeRefreshTokensUseCase {

    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String userId) {
        List<RefreshToken> list = refreshTokenRepository.findByUser_Id(userId);
        for (RefreshToken rt : list) {
            if (!rt.isRevoked()) {
                rt.setRevoked(true);
                refreshTokenRepository.save(rt);
            }
        }
    }
}
