package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimitProperties {

    private boolean enabled;
    private int requestsPerMinute;
    private boolean loginRateLimitEnabled;
    private int loginMaxAttemptsPerMinute;
    private long loginWindowMs = 60_000L;
    private String loginPath = "/auth/login";
    private String rateLimitExceededJson = "{\"error\":\"Too many login attempts\",\"code\":\"RATE_LIMIT_EXCEEDED\"}";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    public boolean isLoginRateLimitEnabled() { return loginRateLimitEnabled; }
    public void setLoginRateLimitEnabled(boolean loginRateLimitEnabled) { this.loginRateLimitEnabled = loginRateLimitEnabled; }
    public int getLoginMaxAttemptsPerMinute() { return loginMaxAttemptsPerMinute; }
    public void setLoginMaxAttemptsPerMinute(int loginMaxAttemptsPerMinute) { this.loginMaxAttemptsPerMinute = loginMaxAttemptsPerMinute; }
    public long getLoginWindowMs() { return loginWindowMs; }
    public void setLoginWindowMs(long loginWindowMs) { this.loginWindowMs = loginWindowMs; }
    public String getLoginPath() { return loginPath; }
    public void setLoginPath(String loginPath) { this.loginPath = loginPath; }
    public String getRateLimitExceededJson() { return rateLimitExceededJson; }
    public void setRateLimitExceededJson(String rateLimitExceededJson) { this.rateLimitExceededJson = rateLimitExceededJson; }
}
