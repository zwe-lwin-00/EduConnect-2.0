package com.educonnect.service;

import com.educonnect.config.AppProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.random.RandomGenerator;

/**
 * Generates temporary passwords for new users (e.g. parent created by admin).
 */
@Component
public class PasswordGenerator {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$%";

    private final AppProperties appProperties;

    public PasswordGenerator(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generate() {
        int len = appProperties.getPasswordGenerationLength();
        RandomGenerator rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(rng.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
