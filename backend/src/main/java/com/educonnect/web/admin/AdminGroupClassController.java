package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.CreateGroupClassRequest;
import com.educonnect.application.admin.dto.GroupClassDto;
import com.educonnect.domain.GroupClass;
import com.educonnect.domain.GroupClassEnrollment;
import com.educonnect.domain.Student;
import com.educonnect.domain.Subscription;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.GroupClassEnrollmentRepository;
import com.educonnect.repository.GroupClassRepository;
import com.educonnect.repository.StudentRepository;
import com.educonnect.repository.SubscriptionRepository;
import com.educonnect.repository.TeacherProfileRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/group-classes")
@RequiredArgsConstructor
public class AdminGroupClassController {

    private final GroupClassRepository groupClassRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final GroupClassEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping
    public List<GroupClassDto> list() {
        return groupClassRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<GroupClassDto> create(@Valid @RequestBody CreateGroupClassRequest req) {
        TeacherProfile teacher = teacherProfileRepository.findById(req.getTeacherId()).orElse(null);
        if (teacher == null) return ResponseEntity.badRequest().build();
        GroupClass gc = GroupClass.builder()
                .name(req.getName())
                .teacher(teacher)
                .active(true)
                .daysOfWeek(req.getDaysOfWeek())
                .scheduleStartTime(req.getScheduleStartTime())
                .scheduleEndTime(req.getScheduleEndTime())
                .build();
        gc = groupClassRepository.save(gc);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(gc));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GroupClassDto> update(@PathVariable String id,
                                                @RequestBody CreateGroupClassRequest req) {
        return groupClassRepository.findById(id)
                .map(gc -> {
                    if (enrollmentRepository.findByGroupClassId(id).isEmpty()) {
                        if (req.getTeacherId() != null) {
                            teacherProfileRepository.findById(req.getTeacherId()).ifPresent(gc::setTeacher);
                        }
                    }
                    if (req.getName() != null) gc.setName(req.getName());
                    if (req.getDaysOfWeek() != null) gc.setDaysOfWeek(req.getDaysOfWeek());
                    if (req.getScheduleStartTime() != null) gc.setScheduleStartTime(req.getScheduleStartTime());
                    if (req.getScheduleEndTime() != null) gc.setScheduleEndTime(req.getScheduleEndTime());
                    groupClassRepository.save(gc);
                    return ResponseEntity.ok(toDto(gc));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/enrollments")
    public ResponseEntity<?> addEnrollment(@PathVariable String id,
                                           @RequestParam String studentId,
                                           @RequestParam(required = false) String subscriptionId) {
        return groupClassRepository.findById(id)
                .flatMap(gc -> studentRepository.findById(studentId).map(student -> {
                    if (enrollmentRepository.existsByGroupClassIdAndStudentId(id, studentId)) {
                        return ResponseEntity.<Object>status(HttpStatus.CONFLICT).body("Already enrolled");
                    }
                    GroupClassEnrollment en = GroupClassEnrollment.builder()
                            .groupClass(gc)
                            .student(student)
                            .build();
                    if (subscriptionId != null && !subscriptionId.isBlank()) {
                        subscriptionRepository.findById(subscriptionId).ifPresent(en::setSubscription);
                    }
                    enrollmentRepository.save(en);
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                }))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/enrollments/{studentId}")
    public ResponseEntity<Void> removeEnrollment(@PathVariable String id, @PathVariable String studentId) {
        enrollmentRepository.findByGroupClassIdAndStudentId(id, studentId).ifPresent(enrollmentRepository::delete);
        return ResponseEntity.noContent().build();
    }

    private GroupClassDto toDto(GroupClass gc) {
        GroupClassDto dto = new GroupClassDto();
        dto.setId(gc.getId());
        dto.setName(gc.getName());
        dto.setTeacherId(gc.getTeacher().getId());
        dto.setTeacherName(gc.getTeacher().getUser().getFullName());
        dto.setActive(gc.isActive());
        dto.setDaysOfWeek(gc.getDaysOfWeek());
        dto.setScheduleStartTime(gc.getScheduleStartTime());
        dto.setScheduleEndTime(gc.getScheduleEndTime());
        dto.setEnrollmentCount(enrollmentRepository.findByGroupClassId(gc.getId()).size());
        return dto;
    }
}
