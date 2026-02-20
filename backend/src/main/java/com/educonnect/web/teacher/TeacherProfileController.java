package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherProfileDto;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.TeacherProfileRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final CurrentUserResolver currentUserResolver;
    private final TeacherProfileRepository teacherProfileRepository;

    @GetMapping
    public ResponseEntity<TeacherProfileDto> get() {
        TeacherProfile t = currentUserResolver.requireCurrentTeacher();
        return ResponseEntity.ok(toDto(t));
    }

    /** Update teacher's own profile; only zoomJoinUrl (default for One-To-One) is updatable by teacher. */
    @PatchMapping
    public ResponseEntity<TeacherProfileDto> update(@RequestBody Map<String, String> body) {
        TeacherProfile t = currentUserResolver.requireCurrentTeacher();
        if (body.containsKey("zoomJoinUrl")) {
            t.setZoomJoinUrl(body.get("zoomJoinUrl"));
            teacherProfileRepository.save(t);
        }
        return ResponseEntity.ok(toDto(t));
    }

    private TeacherProfileDto toDto(TeacherProfile t) {
        TeacherProfileDto dto = new TeacherProfileDto();
        dto.setId(t.getId());
        dto.setEmail(t.getUser().getEmail());
        dto.setFullName(t.getUser().getFullName());
        dto.setPhone(t.getUser().getPhone());
        dto.setEducation(t.getEducation());
        dto.setBio(t.getBio());
        dto.setSpecializations(t.getSpecializations());
        dto.setVerificationStatus(t.getVerificationStatus().name());
        dto.setZoomJoinUrl(t.getZoomJoinUrl());
        return dto;
    }
}
