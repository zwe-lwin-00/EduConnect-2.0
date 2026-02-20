package com.educonnect.web.common;

import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves current authenticated user id and optional teacher profile.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final TeacherProfileRepository teacherProfileRepository;

    public String getCurrentUserId() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (name == null || "anonymousUser".equals(name)) {
            return null;
        }
        return name;
    }

    /**
     * @return teacher profile for current user, or null if not a teacher
     */
    public TeacherProfile getCurrentTeacherOrNull() {
        String userId = getCurrentUserId();
        if (userId == null) return null;
        return teacherProfileRepository.findByUser_Id(userId).orElse(null);
    }

    /**
     * @throws IllegalStateException if current user is not a teacher
     */
    public TeacherProfile requireCurrentTeacher() {
        TeacherProfile t = getCurrentTeacherOrNull();
        if (t == null) {
            throw new IllegalStateException("Current user is not a teacher");
        }
        return t;
    }
}
