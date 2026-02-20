package com.educonnect.application.auth.port;

import java.util.List;

/**
 * Port for creating JWT tokens. Implemented by security adapter.
 */
public interface TokenPort {

    String createAccessToken(String userId, String email, List<String> roles);

    String createRefreshToken(String userId);

    long getAccessTokenExpirationSeconds();
}
