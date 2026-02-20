package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherGradeDto;
import com.educonnect.domain.StudentGrade;
import com.educonnect.repository.StudentGradeRepository;
import com.educonnect.repository.StudentRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/grades")
@RequiredArgsConstructor
public class TeacherGradesController {

    private final CurrentUserResolver currentUserResolver;
    private final StudentGradeRepository gradeRepository;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<TeacherGradeDto>> list(@RequestParam(required = false) String studentId) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<StudentGrade> list = studentId != null && !studentId.isBlank()
                ? gradeRepository.findByStudent_Id(studentId).stream()
                    .filter(g -> g.getTeacher().getId().equals(teacherId))
                    .collect(Collectors.toList())
                : gradeRepository.findByTeacher_Id(teacherId);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<TeacherGradeDto> create(@RequestBody Map<String, Object> body) {
        var teacher = currentUserResolver.requireCurrentTeacher();
        String studentId = (String) body.get("studentId");
        String title = (String) body.get("title");
        Object gv = body.get("gradeValue");
        Object maxVal = body.get("maxValue");
        String gradeDateStr = (String) body.get("gradeDate");
        String notes = (String) body.get("notes");
        if (studentId == null || title == null || gv == null) {
            return ResponseEntity.badRequest().build();
        }
        var student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return ResponseEntity.badRequest().build();
        double gradeValue = gv instanceof Number ? ((Number) gv).doubleValue() : Double.parseDouble(gv.toString());
        Double maxValue = maxVal != null ? (maxVal instanceof Number ? ((Number) maxVal).doubleValue() : Double.parseDouble(maxVal.toString())) : null;
        LocalDate gradeDate = gradeDateStr != null ? LocalDate.parse(gradeDateStr) : LocalDate.now();
        StudentGrade g = StudentGrade.builder()
                .teacher(teacher)
                .student(student)
                .title(title)
                .gradeValue(gradeValue)
                .maxValue(maxValue)
                .gradeDate(gradeDate)
                .notes(notes != null ? notes : "")
                .build();
        g = gradeRepository.save(g);
        return ResponseEntity.ok(toDto(g));
    }

    private TeacherGradeDto toDto(StudentGrade g) {
        return new TeacherGradeDto(
                g.getId(),
                g.getStudent().getId(),
                g.getStudent().getFullName(),
                g.getTitle(),
                g.getGradeValue(),
                g.getMaxValue(),
                g.getGradeDate(),
                g.getNotes()
        );
    }
}
