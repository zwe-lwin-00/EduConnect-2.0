package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "seed-data")
public class SeedDataProperties {

    private Admin admin = new Admin();
    private List<String> roles; // from seed-data.roles in application.yml

    public static class Admin {
        private String email;   // from seed-data.admin.email
        private String password;   // from seed-data.admin.password
        private String fullName;   // from seed-data.admin.full-name

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
