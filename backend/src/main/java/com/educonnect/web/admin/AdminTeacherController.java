package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.CreateTeacherRequest;
import com.educonnect.application.admin.dto.TeacherDto;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.ApplicationUserRepository;
import com.educonnect.repository.TeacherProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.educonnect.service.PasswordGenerator;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final TeacherProfileRepository teacherProfileRepository;
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;

    @GetMapping
    public List<TeacherDto> list(@RequestParam(required = false) String verificationStatus) {
        List<TeacherProfile> list = verificationStatus != null && !verificationStatus.isBlank()
                ? teacherProfileRepository.findByVerificationStatus(TeacherProfile.VerificationStatus.valueOf(verificationStatus))
                : teacherProfileRepository.findAllWithUser();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDto> get(@PathVariable String id) {
        return teacherProfileRepository.findById(id)
                .map(t -> ResponseEntity.ok(toDto(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> onboard(@Valid @RequestBody CreateTeacherRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        String tempPassword = passwordGenerator.generate();
        ApplicationUser user = ApplicationUser.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .roles(java.util.Set.of("TEACHER"))
                .mustChangePassword(true)
                .active(true)
                .build();
        user = userRepository.save(user);
        TeacherProfile profile = TeacherProfile.builder()
                .user(user)
                .nrcEncrypted(req.getNrc())
                .education(req.getEducation())
                .bio(req.getBio())
                .specializations(req.getSpecializations() != null ? new java.util.HashSet<>(req.getSpecializations()) : new java.util.HashSet<>())
                .verificationStatus(TeacherProfile.VerificationStatus.PENDING)
                .build();
        profile = teacherProfileRepository.save(profile);
        TeacherDto dto = toDto(profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "teacher", dto,
                "temporaryPassword", tempPassword
        ));
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String id) {
        return teacherProfileRepository.findById(id)
                .map(p -> {
                    ApplicationUser u = p.getUser();
                    String tempPassword = passwordGenerator.generate();
                    u.setPasswordHash(passwordEncoder.encode(tempPassword));
                    u.setMustChangePassword(true);
                    userRepository.save(u);
                    return ResponseEntity.ok(Map.of(
                            "message", "Password reset; teacher must change on next login",
                            "temporaryPassword", tempPassword,
                            "email", u.getEmail()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeacherDto> update(@PathVariable String id, @RequestBody CreateTeacherRequest req) {
        return teacherProfileRepository.findById(id)
                .map(p -> {
                    ApplicationUser u = p.getUser();
                    if (req.getFullName() != null) u.setFullName(req.getFullName());
                    if (req.getPhone() != null) u.setPhone(req.getPhone());
                    if (req.getEducation() != null) p.setEducation(req.getEducation());
                    if (req.getBio() != null) p.setBio(req.getBio());
                    if (req.getSpecializations() != null) p.setSpecializations(new java.util.HashSet<>(req.getSpecializations()));
                    userRepository.save(u);
                    teacherProfileRepository.save(p);
                    return ResponseEntity.ok(toDto(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<TeacherDto> verify(@PathVariable String id) {
        return setVerification(id, TeacherProfile.VerificationStatus.VERIFIED);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TeacherDto> reject(@PathVariable String id) {
        return setVerification(id, TeacherProfile.VerificationStatus.REJECTED);
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<TeacherDto> activate(@PathVariable String id) {
        return setActive(id, true);
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<TeacherDto> suspend(@PathVariable String id) {
        return setActive(id, false);
    }

    private ResponseEntity<TeacherDto> setVerification(String id, TeacherProfile.VerificationStatus status) {
        return teacherProfileRepository.findById(id)
                .map(p -> {
                    p.setVerificationStatus(status);
                    teacherProfileRepository.save(p);
                    return ResponseEntity.ok(toDto(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<TeacherDto> setActive(String id, boolean active) {
        return teacherProfileRepository.findById(id)
                .map(p -> {
                    p.getUser().setActive(active);
                    userRepository.save(p.getUser());
                    return ResponseEntity.ok(toDto(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private TeacherDto toDto(TeacherProfile p) {
        ApplicationUser u = p.getUser();
        TeacherDto dto = new TeacherDto();
        dto.setId(p.getId());
        dto.setUserId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setFullName(u.getFullName());
        dto.setPhone(u.getPhone());
        dto.setEducation(p.getEducation());
        dto.setBio(p.getBio());
        dto.setSpecializations(p.getSpecializations());
        dto.setVerificationStatus(p.getVerificationStatus().name());
        dto.setHourlyRate(p.getHourlyRate());
        dto.setActive(u.isActive());
        return dto;
    }
}
