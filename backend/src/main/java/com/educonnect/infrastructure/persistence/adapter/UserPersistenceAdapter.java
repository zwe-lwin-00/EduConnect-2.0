package com.educonnect.infrastructure.persistence.adapter;

import com.educonnect.application.auth.port.LoadUserPort;
import com.educonnect.application.auth.port.UpdateUserPort;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.repository.ApplicationUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence adapter: loads and saves users via JPA repository. Implements application ports.
 */
@Component
public class UserPersistenceAdapter implements LoadUserPort, UpdateUserPort {

    private final ApplicationUserRepository repository;

    public UserPersistenceAdapter(ApplicationUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<ApplicationUser> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<ApplicationUser> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public void save(ApplicationUser user) {
        repository.save(user);
    }
}
