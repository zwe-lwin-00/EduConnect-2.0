package com.educonnect.application.auth.port;

import com.educonnect.domain.ApplicationUser;

/**
 * Port for persisting user updates (e.g. password change). Implemented by persistence adapter.
 */
public interface UpdateUserPort {

    void save(ApplicationUser user);
}
