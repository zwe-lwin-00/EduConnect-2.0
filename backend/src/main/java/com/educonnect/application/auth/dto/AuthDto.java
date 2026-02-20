package com.educonnect.application.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request/response DTOs for auth feature. API contract for auth endpoints.
 */
public final class AuthDto {

    private AuthDto() {}

    public static class LoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;
        @NotBlank
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class UserResponse {
        private String id;
        private String email;
        private String fullName;
        private java.util.List<String> roles;
        private boolean mustChangePassword;

        public UserResponse() {}
        public UserResponse(String id, String email, String fullName, java.util.List<String> roles, boolean mustChangePassword) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.roles = roles;
            this.mustChangePassword = mustChangePassword;
        }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public java.util.List<String> getRoles() { return roles; }
        public void setRoles(java.util.List<String> roles) { this.roles = roles; }
        public boolean isMustChangePassword() { return mustChangePassword; }
        public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
    }

    public static class LoginResponse {
        private String accessToken;
        private String refreshToken;
        private long expiresIn;
        private UserResponse user;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
        public UserResponse getUser() { return user; }
        public void setUser(UserResponse user) { this.user = user; }
    }
}
