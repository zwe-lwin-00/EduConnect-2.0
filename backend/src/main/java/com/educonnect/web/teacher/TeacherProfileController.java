package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherProfileDto;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher/profile")
@RequiredArgsConstructor
public class TeacherProfileController {

    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public ResponseEntity<TeacherProfileDto> get() {
        TeacherProfile t = currentUserResolver.requireCurrentTeacher();
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
        return ResponseEntity.ok(dto);
    }
}
