package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherHomeworkDto;
import com.educonnect.domain.ContractSession;
import com.educonnect.domain.GroupClassEnrollment;
import com.educonnect.domain.Homework;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.repository.GroupClassEnrollmentRepository;
import com.educonnect.repository.GroupClassRepository;
import com.educonnect.repository.HomeworkRepository;
import com.educonnect.repository.StudentRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/homework")
@RequiredArgsConstructor
public class TeacherHomeworkController {

    private final CurrentUserResolver currentUserResolver;
    private final HomeworkRepository homeworkRepository;
    private final StudentRepository studentRepository;
    private final ContractSessionRepository contractSessionRepository;
    private final GroupClassRepository groupClassRepository;
    private final GroupClassEnrollmentRepository enrollmentRepository;

    @GetMapping
    public ResponseEntity<List<TeacherHomeworkDto>> list(
            @RequestParam(required = false) String studentId) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<Homework> list = studentId != null && !studentId.isBlank()
                ? homeworkRepository.findByTeacher_IdAndStudent_Id(teacherId, studentId)
                : homeworkRepository.findByTeacher_Id(teacherId);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<TeacherHomeworkDto> create(@RequestBody Map<String, Object> body) {
        var teacher = currentUserResolver.requireCurrentTeacher();
        String studentId = (String) body.get("studentId");
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        String dueDateStr = (String) body.get("dueDate");
        if (studentId == null || title == null || dueDateStr == null) {
            return ResponseEntity.badRequest().build();
        }
        var student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return ResponseEntity.badRequest().build();
        Set<String> allowedStudentIds = new HashSet<>();
        for (ContractSession c : contractSessionRepository.findByTeacher_IdAndStatus(teacher.getId(), ContractSession.ContractStatus.ACTIVE)) {
            allowedStudentIds.add(c.getStudent().getId());
        }
        for (var gc : groupClassRepository.findByTeacher_IdAndActiveTrue(teacher.getId())) {
            for (GroupClassEnrollment en : enrollmentRepository.findByGroupClassId(gc.getId())) {
                allowedStudentIds.add(en.getStudent().getId());
            }
        }
        if (!allowedStudentIds.contains(studentId)) {
            return ResponseEntity.status(403).build();
        }
        Homework h = Homework.builder()
                .teacher(teacher)
                .student(student)
                .title(title)
                .description(description != null ? description : "")
                .dueDate(LocalDate.parse(dueDateStr))
                .status(Homework.HomeworkStatus.ASSIGNED)
                .build();
        h = homeworkRepository.save(h);
        return ResponseEntity.ok(toDto(h));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TeacherHomeworkDto> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        return homeworkRepository.findById(id)
                .filter(h -> h.getTeacher().getId().equals(teacherId))
                .map(h -> {
                    if (body.containsKey("status")) {
                        try {
                            h.setStatus(Homework.HomeworkStatus.valueOf((String) body.get("status")));
                        } catch (Exception ignored) {}
                    }
                    if (body.containsKey("teacherFeedback")) h.setTeacherFeedback((String) body.get("teacherFeedback"));
                    homeworkRepository.save(h);
                    return ResponseEntity.ok(toDto(h));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private TeacherHomeworkDto toDto(Homework h) {
        return new TeacherHomeworkDto(
                h.getId(),
                h.getStudent().getId(),
                h.getStudent().getFullName(),
                h.getTitle(),
                h.getDescription(),
                h.getDueDate(),
                h.getStatus().name(),
                h.getTeacherFeedback()
        );
    }
}
