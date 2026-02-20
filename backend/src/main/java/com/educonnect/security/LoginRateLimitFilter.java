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

    public LoginRateLimitFilter(RateLimitProperties config) {
        this.config = config;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!config.isLoginRateLimitEnabled()) return true;
        return !"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().equals(config.getLoginPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = clientIp(request);
        SlidingWindow window = attemptsByIp.computeIfAbsent(clientIp, k -> new SlidingWindow(config.getLoginWindowMs()));
        if (!window.tryAcquire(config.getLoginMaxAttemptsPerMinute())) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(config.getRateLimitExceededJson());
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
        private final long windowMs;

        SlidingWindow(long windowMs) {
            this.windowMs = windowMs;
        }

        synchronized boolean tryAcquire(int maxPerWindow) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMs) {
                windowStart = now;
                count.set(0);
            }
            return count.incrementAndGet() <= maxPerWindow;
        }
    }
}
