package com.educonnect.config;

import com.educonnect.security.LoginRateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public LoginRateLimitFilter loginRateLimitFilter(RateLimitProperties rateLimitProperties) {
        return new LoginRateLimitFilter(rateLimitProperties);
    }
}
