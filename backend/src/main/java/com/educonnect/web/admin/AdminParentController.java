package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.CreateParentRequest;
import com.educonnect.application.admin.dto.CreateParentResponse;
import com.educonnect.application.admin.dto.ParentDto;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.repository.ApplicationUserRepository;
import com.educonnect.service.PasswordGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/parents")
@RequiredArgsConstructor
public class AdminParentController {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;

    @GetMapping
    public List<ParentDto> list() {
        return userRepository.findByRolesContaining("PARENT").stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<CreateParentResponse> create(@Valid @RequestBody CreateParentRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        String tempPassword = passwordGenerator.generate();
        ApplicationUser user = ApplicationUser.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .roles(java.util.Set.of("PARENT"))
                .mustChangePassword(true)
                .active(true)
                .build();
        user = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateParentResponse(user.getId(), user.getEmail(), tempPassword));
    }

    private ParentDto toDto(ApplicationUser u) {
        ParentDto dto = new ParentDto();
        dto.setId(u.getId());
        dto.setEmail(u.getEmail());
        dto.setFullName(u.getFullName());
        dto.setPhone(u.getPhone());
        dto.setActive(u.isActive());
        return dto;
    }
}
