package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimitProperties {

    private boolean enabled = false;
    private int requestsPerMinute = 60;
    private boolean loginRateLimitEnabled = true;
    private int loginMaxAttemptsPerMinute = 10;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(int requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    public boolean isLoginRateLimitEnabled() { return loginRateLimitEnabled; }
    public void setLoginRateLimitEnabled(boolean loginRateLimitEnabled) { this.loginRateLimitEnabled = loginRateLimitEnabled; }
    public int getLoginMaxAttemptsPerMinute() { return loginMaxAttemptsPerMinute; }
    public void setLoginMaxAttemptsPerMinute(int loginMaxAttemptsPerMinute) { this.loginMaxAttemptsPerMinute = loginMaxAttemptsPerMinute; }
}
