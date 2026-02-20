package com.educonnect.application.auth.port;

import com.educonnect.domain.ApplicationUser;

import java.util.Optional;

/**
 * Port for loading user by email or id. Implemented by persistence adapter.
 */
public interface LoadUserPort {

    Optional<ApplicationUser> findByEmail(String email);

    Optional<ApplicationUser> findById(String id);
}
