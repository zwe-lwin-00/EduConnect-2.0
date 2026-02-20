package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimitProperties {

    private boolean enabled;   // from rate-limiting.enabled
    private int requestsPerMinute;   // from rate-limiting.requests-per-minute
    private boolean loginRateLimitEnabled;   // from rate-limiting.login-rate-limit-enabled
    private int loginMaxAttemptsPerMinute;   // from rate-limiting.login-max-attempts-per-minute

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    public boolean isLoginRateLimitEnabled() { return loginRateLimitEnabled; }
    public void setLoginRateLimitEnabled(boolean loginRateLimitEnabled) { this.loginRateLimitEnabled = loginRateLimitEnabled; }
    public int getLoginMaxAttemptsPerMinute() { return loginMaxAttemptsPerMinute; }
    public void setLoginMaxAttemptsPerMinute(int loginMaxAttemptsPerMinute) { this.loginMaxAttemptsPerMinute = loginMaxAttemptsPerMinute; }
}
