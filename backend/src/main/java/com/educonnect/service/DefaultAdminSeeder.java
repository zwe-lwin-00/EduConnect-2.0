package com.educonnect.service;

import com.educonnect.config.SeedDataProperties;
import com.educonnect.config.SecurityProperties;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.repository.ApplicationUserRepository;
import com.educonnect.shared.logging.LoggerExtensions;
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
    private final SecurityProperties securityProperties;

    public DefaultAdminSeeder(ApplicationUserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              SeedDataProperties seedData,
                              SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedData = seedData;
        this.securityProperties = securityProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedDefaultAdmin() {
        String email = seedData.getAdmin().getEmail();
        LoggerExtensions.info(log, "EduConnect startup: default admin check ({}). Created on first run if missing.", email);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            LoggerExtensions.info(log, "Default admin already exists. Skipping seed.");
            return;
        }
        String adminRole = securityProperties.getRoles().getAdmin();
        ApplicationUser admin = ApplicationUser.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(seedData.getAdmin().getPassword()))
                .fullName(seedData.getAdmin().getFullName())
                .roles(Set.of(adminRole))
                .mustChangePassword(false)
                .active(true)
                .build();
        userRepository.save(admin);
        LoggerExtensions.info(log, "Default admin account CREATED. Login at {} with: {} / (password in seed-data.admin)", securityProperties.getAuth().getLoginPath(), email);
    }
}
