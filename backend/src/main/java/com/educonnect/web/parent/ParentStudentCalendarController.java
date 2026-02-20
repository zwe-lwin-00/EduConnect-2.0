package com.educonnect.web.parent;

import com.educonnect.application.parent.ParentStudentCalendarService;
import com.educonnect.application.teacher.dto.TeacherCalendarResponseDto;
import com.educonnect.domain.Student;
import com.educonnect.repository.StudentRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parent/students")
@RequiredArgsConstructor
public class ParentStudentCalendarController {

    private final CurrentUserResolver currentUserResolver;
    private final StudentRepository studentRepository;
    private final ParentStudentCalendarService parentStudentCalendarService;

    @GetMapping("/{studentId}/calendar")
    public ResponseEntity<TeacherCalendarResponseDto> getCalendar(
            @PathVariable String studentId,
            @RequestParam int year,
            @RequestParam int month) {
        String parentId = currentUserResolver.getCurrentUserId();
        if (parentId == null) return ResponseEntity.status(401).build();
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null || !student.getParent().getId().equals(parentId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(parentStudentCalendarService.getCalendar(studentId, year, month));
    }
}
