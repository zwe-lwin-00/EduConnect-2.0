package com.educonnect.application.auth.usecase;

import com.educonnect.application.auth.port.LoadUserPort;
import com.educonnect.application.auth.port.UpdateUserPort;
import com.educonnect.domain.ApplicationUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Changes the current user's password. Caller must supply the authenticated userId.
 * Returns false if user not found or current password does not match.
 */
@Service
public class ChangePasswordUseCase {

    private final LoadUserPort loadUserPort;
    private final UpdateUserPort updateUserPort;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(LoadUserPort loadUserPort, UpdateUserPort updateUserPort, PasswordEncoder passwordEncoder) {
        this.loadUserPort = loadUserPort;
        this.updateUserPort = updateUserPort;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return true if password was changed, false if user not found or current password invalid
     */
    public boolean execute(String userId, String currentPassword, String newPassword) {
        if (userId == null || currentPassword == null || newPassword == null) return false;
        ApplicationUser user = loadUserPort.findById(userId).orElse(null);
        if (user == null) return false;
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) return false;
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        updateUserPort.save(user);
        return true;
    }
}
