package com.educonnect.security;

import com.educonnect.config.RateLimitProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limits POST /auth/login by client IP to mitigate brute-force attempts.
 */
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties config;
    private final ConcurrentHashMap<String, SlidingWindow> attemptsByIp = new ConcurrentHashMap<>();
    private static final long WINDOW_MS = 60_000L; // 1 minute

    public LoginRateLimitFilter(RateLimitProperties config) {
        this.config = config;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!config.isLoginRateLimitEnabled()) return true;
        return !"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().equals("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = clientIp(request);
        SlidingWindow window = attemptsByIp.computeIfAbsent(clientIp, k -> new SlidingWindow());
        if (!window.tryAcquire(config.getLoginMaxAttemptsPerMinute())) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many login attempts\",\"code\":\"RATE_LIMIT_EXCEEDED\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    private static final class SlidingWindow {
        private long windowStart = System.currentTimeMillis();
        private final AtomicInteger count = new AtomicInteger(0);

        synchronized boolean tryAcquire(int maxPerMinute) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= WINDOW_MS) {
                windowStart = now;
                count.set(0);
            }
            return count.incrementAndGet() <= maxPerMinute;
        }
    }
}
