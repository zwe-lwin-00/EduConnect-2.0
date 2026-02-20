package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.ContractDto;
import com.educonnect.application.admin.dto.CreateContractRequest;
import com.educonnect.application.shared.ScheduleValidator;
import com.educonnect.domain.ContractSession;
import com.educonnect.domain.Student;
import com.educonnect.domain.Subscription;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.repository.StudentRepository;
import com.educonnect.repository.SubscriptionRepository;
import com.educonnect.repository.TeacherProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/contracts")
@RequiredArgsConstructor
public class AdminContractController {

    private final ContractSessionRepository contractRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final StudentRepository studentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping
    public List<ContractDto> list(@RequestParam(required = false) String status) {
        List<ContractSession> list = contractRepository.findAll();
        if (status != null && !status.isBlank()) {
            try {
                ContractSession.ContractStatus s = ContractSession.ContractStatus.valueOf(status);
                list = list.stream().filter(c -> c.getStatus() == s).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ContractDto> create(@Valid @RequestBody CreateContractRequest req) {
        ScheduleValidator.validateDaysOfWeek(req.getDaysOfWeek());
        ScheduleValidator.validateScheduleTimes(req.getScheduleStartTime(), req.getScheduleEndTime());
        TeacherProfile teacher = teacherProfileRepository.findById(req.getTeacherId()).orElse(null);
        Student student = studentRepository.findById(req.getStudentId()).orElse(null);
        if (teacher == null || student == null) return ResponseEntity.badRequest().build();
        Subscription sub = null;
        LocalDate legacyEnd = null;
        if (req.getSubscriptionId() != null && !req.getSubscriptionId().isBlank()) {
            sub = subscriptionRepository.findById(req.getSubscriptionId()).orElse(null);
            if (sub == null) return ResponseEntity.badRequest().build();
        } else if (req.getLegacyPeriodEnd() != null) {
            legacyEnd = req.getLegacyPeriodEnd();
        } else {
            return ResponseEntity.badRequest().build();
        }
        ContractSession contract = ContractSession.builder()
                .teacher(teacher)
                .student(student)
                .subscription(sub)
                .legacyPeriodEnd(legacyEnd)
                .daysOfWeek(req.getDaysOfWeek())
                .scheduleStartTime(req.getScheduleStartTime())
                .scheduleEndTime(req.getScheduleEndTime())
                .status(ContractSession.ContractStatus.ACTIVE)
                .build();
        contract = contractRepository.save(contract);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(contract));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ContractDto> cancel(@PathVariable String id) {
        return contractRepository.findById(id)
                .map(c -> {
                    c.setStatus(ContractSession.ContractStatus.CANCELLED);
                    contractRepository.save(c);
                    return ResponseEntity.ok(toDto(c));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ContractDto toDto(ContractSession c) {
        ContractDto dto = new ContractDto();
        dto.setId(c.getId());
        dto.setTeacherId(c.getTeacher().getId());
        dto.setTeacherName(c.getTeacher().getUser().getFullName());
        dto.setStudentId(c.getStudent().getId());
        dto.setStudentName(c.getStudent().getFullName());
        dto.setSubscriptionId(c.getSubscription() != null ? c.getSubscription().getId() : null);
        dto.setLegacyPeriodEnd(c.getLegacyPeriodEnd());
        dto.setDaysOfWeek(c.getDaysOfWeek());
        dto.setScheduleStartTime(c.getScheduleStartTime());
        dto.setScheduleEndTime(c.getScheduleEndTime());
        dto.setStatus(c.getStatus().name());
        return dto;
    }
}
