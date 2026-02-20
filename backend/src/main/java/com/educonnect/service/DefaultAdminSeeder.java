package com.educonnect.service;

import com.educonnect.config.SeedDataProperties;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Creates the default admin account when the API starts for the first time.
 * Check the API console output for confirmation.
 */
@Component
public class DefaultAdminSeeder {

    private static final Logger log = LoggerFactory.getLogger(DefaultAdminSeeder.class);

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedDataProperties seedData;

    public DefaultAdminSeeder(ApplicationUserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              SeedDataProperties seedData) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedData = seedData;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDefaultAdmin() {
        String email = seedData.getAdmin().getEmail();
        log.info("EduConnect startup: default admin check ({}). Created on first run if missing.", email);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("Default admin already exists. Skipping seed.");
            return;
        }
        ApplicationUser admin = ApplicationUser.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(seedData.getAdmin().getPassword()))
                .fullName(seedData.getAdmin().getFullName())
                .roles(Set.of("ADMIN"))
                .mustChangePassword(false)
                .active(true)
                .build();
        userRepository.save(admin);
        log.info("Default admin account CREATED. Login at /auth/login with: {} / (password in seed-data.admin)", email);
    }
}
