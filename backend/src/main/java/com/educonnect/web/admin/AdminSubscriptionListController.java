package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.CreateSubscriptionRequest;
import com.educonnect.application.admin.dto.SubscriptionDto;
import com.educonnect.domain.Student;
import com.educonnect.domain.Subscription;
import com.educonnect.repository.StudentRepository;
import com.educonnect.repository.SubscriptionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
public class AdminSubscriptionListController {

    private final SubscriptionRepository subscriptionRepository;
    private final StudentRepository studentRepository;

    @GetMapping
    public List<SubscriptionDto> list(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        List<Subscription> list = subscriptionRepository.findAll();
        if (studentId != null && !studentId.isBlank()) {
            list = list.stream().filter(s -> s.getStudent().getId().equals(studentId)).collect(Collectors.toList());
        }
        if (type != null && !type.isBlank()) {
            try {
                Subscription.SubscriptionType t = Subscription.SubscriptionType.valueOf(type);
                list = list.stream().filter(s -> s.getType() == t).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        if (status != null && !status.isBlank()) {
            try {
                Subscription.SubscriptionStatus st = Subscription.SubscriptionStatus.valueOf(status);
                list = list.stream().filter(s -> s.getStatus() == st).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<SubscriptionDto> create(@Valid @RequestBody CreateSubscriptionRequest req) {
        Student student = studentRepository.findById(req.getStudentId()).orElse(null);
        if (student == null) return ResponseEntity.badRequest().build();
        Subscription.SubscriptionType type;
        try {
            type = Subscription.SubscriptionType.valueOf(req.getType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        java.time.LocalDate startDate = req.getStartDate();
        java.time.LocalDate endDate = req.getEndDate() != null ? req.getEndDate() : startDate.plusMonths(1);
        Subscription sub = Subscription.builder()
                .student(student)
                .type(type)
                .startDate(startDate)
                .endDate(endDate)
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .build();
        sub = subscriptionRepository.save(sub);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(sub));
    }

    private SubscriptionDto toDto(Subscription s) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(s.getId());
        dto.setStudentId(s.getStudent().getId());
        dto.setStudentName(s.getStudent().getFullName());
        dto.setType(s.getType().name());
        dto.setStartDate(s.getStartDate());
        dto.setEndDate(s.getEndDate());
        dto.setStatus(s.getStatus().name());
        return dto;
    }
}
