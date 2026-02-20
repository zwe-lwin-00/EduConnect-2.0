package com.educonnect.web.teacher;

import com.educonnect.application.shared.ContractEnrollmentAccess;
import com.educonnect.application.teacher.dto.TeacherGroupClassDto;
import com.educonnect.domain.ContractSession;
import com.educonnect.domain.GroupClass;
import com.educonnect.domain.GroupClassEnrollment;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.repository.GroupClassEnrollmentRepository;
import com.educonnect.repository.GroupClassRepository;
import com.educonnect.repository.StudentRepository;
import com.educonnect.repository.SubscriptionRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/group-classes")
@RequiredArgsConstructor
public class TeacherGroupClassController {

    private final CurrentUserResolver currentUserResolver;
    private final GroupClassRepository groupClassRepository;
    private final GroupClassEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ContractSessionRepository contractSessionRepository;

    @GetMapping
    public ResponseEntity<List<TeacherGroupClassDto>> list() {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<TeacherGroupClassDto> list = groupClassRepository.findByTeacher_Id(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeacherGroupClassDto> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        return groupClassRepository.findById(id)
                .filter(gc -> gc.getTeacher().getId().equals(teacherId))
                .map(gc -> {
                    if (body.containsKey("name") && body.get("name") != null) gc.setName((String) body.get("name"));
                    if (body.containsKey("zoomJoinUrl")) gc.setZoomJoinUrl((String) body.get("zoomJoinUrl"));
                    if (body.containsKey("active")) gc.setActive(Boolean.TRUE.equals(body.get("active")));
                    groupClassRepository.save(gc);
                    return ResponseEntity.ok(toDto(gc));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/enrollments")
    public ResponseEntity<Void> addEnrollment(
            @PathVariable String id,
            @RequestParam String studentId,
            @RequestParam(required = false) String subscriptionId,
            @RequestParam(required = false) String contractId) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        return groupClassRepository.findById(id)
                .filter(gc -> gc.getTeacher().getId().equals(teacherId))
                .flatMap(gc -> studentRepository.findById(studentId).map(student -> {
                    if (enrollmentRepository.existsByGroupClassIdAndStudentId(id, studentId)) {
                        return ResponseEntity.<Void>status(HttpStatus.CONFLICT).build();
                    }
                    GroupClassEnrollment en = GroupClassEnrollment.builder()
                            .groupClass(gc)
                            .student(student)
                            .build();
                    if (subscriptionId != null && !subscriptionId.isBlank()) {
                        subscriptionRepository.findById(subscriptionId).ifPresent(en::setSubscription);
                    }
                    if (contractId != null && !contractId.isBlank()) {
                        ContractSession contract = contractSessionRepository.findById(contractId).orElse(null);
                        if (contract == null || !contract.getTeacher().getId().equals(teacherId)
                                || !contract.getStudent().getId().equals(studentId)) {
                            return ResponseEntity.badRequest().build();
                        }
                        if (!ContractEnrollmentAccess.hasAccess(contract)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                        en.setContract(contract);
                    }
                    enrollmentRepository.save(en);
                    return ResponseEntity.status(HttpStatus.CREATED).<Void>build();
                }))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/enrollments/{studentId}")
    public ResponseEntity<Void> removeEnrollment(@PathVariable String id, @PathVariable String studentId) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        return groupClassRepository.findById(id)
                .filter(gc -> gc.getTeacher().getId().equals(teacherId))
                .flatMap(gc -> enrollmentRepository.findByGroupClassIdAndStudentId(id, studentId)
                        .map(en -> {
                            enrollmentRepository.delete(en);
                            return ResponseEntity.<Void>noContent().build();
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    private TeacherGroupClassDto toDto(GroupClass gc) {
        return TeacherGroupClassDto.builder()
                .id(gc.getId())
                .name(gc.getName())
                .zoomJoinUrl(gc.getZoomJoinUrl())
                .active(gc.isActive())
                .daysOfWeek(gc.getDaysOfWeek())
                .scheduleStartTime(gc.getScheduleStartTime())
                .scheduleEndTime(gc.getScheduleEndTime())
                .scheduleUpdatedAt(gc.getScheduleUpdatedAt())
                .enrollmentCount(enrollmentRepository.findByGroupClassId(gc.getId()).size())
                .build();
    }
}
