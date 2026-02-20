package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private final Roles roles = new Roles();
    private final Auth auth = new Auth();

    public Roles getRoles() {
        return roles;
    }

    public Auth getAuth() {
        return auth;
    }

    public static class Roles {
        private String admin = "ADMIN";
        private String teacher = "TEACHER";
        private String parent = "PARENT";

        public String getAdmin() { return admin; }
        public void setAdmin(String admin) { this.admin = admin; }
        public String getTeacher() { return teacher; }
        public void setTeacher(String teacher) { this.teacher = teacher; }
        public String getParent() { return parent; }
        public void setParent(String parent) { this.parent = parent; }
    }

    public static class Auth {
        private String loginPath = "/auth/login";
        private String refreshPath = "/auth/refresh";
        private List<String> permitAllPathPatterns = List.of(
                "/auth/login", "/auth/refresh",
                "/config", "/config/**",
                "/actuator/health", "/actuator/health/**", "/actuator/info"
        );
        private String adminPathPattern = "/admin/**";
        private String teacherPathPattern = "/teacher/**";
        private String parentPathPattern = "/parent/**";

        public String getLoginPath() { return loginPath; }
        public void setLoginPath(String loginPath) { this.loginPath = loginPath; }
        public String getRefreshPath() { return refreshPath; }
        public void setRefreshPath(String refreshPath) { this.refreshPath = refreshPath; }
        public List<String> getPermitAllPathPatterns() { return permitAllPathPatterns; }
        public void setPermitAllPathPatterns(List<String> permitAllPathPatterns) { this.permitAllPathPatterns = permitAllPathPatterns; }
        public String getAdminPathPattern() { return adminPathPattern; }
        public void setAdminPathPattern(String adminPathPattern) { this.adminPathPattern = adminPathPattern; }
        public String getTeacherPathPattern() { return teacherPathPattern; }
        public void setTeacherPathPattern(String teacherPathPattern) { this.teacherPathPattern = teacherPathPattern; }
        public String getParentPathPattern() { return parentPathPattern; }
        public void setParentPathPattern(String parentPathPattern) { this.parentPathPattern = parentPathPattern; }
    }
}
