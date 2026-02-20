package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.CreateStudentRequest;
import com.educonnect.application.admin.dto.StudentDto;
import com.educonnect.domain.ApplicationUser;
import com.educonnect.domain.Student;
import com.educonnect.repository.ApplicationUserRepository;
import com.educonnect.repository.StudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentRepository studentRepository;
    private final ApplicationUserRepository userRepository;

    @GetMapping
    public List<StudentDto> list(@RequestParam(required = false) String parentId) {
        List<Student> list = parentId != null && !parentId.isBlank()
                ? studentRepository.findByParent_Id(parentId)
                : studentRepository.findAll();
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<StudentDto> create(@Valid @RequestBody CreateStudentRequest req) {
        ApplicationUser parent = userRepository.findById(req.getParentId()).orElse(null);
        if (parent == null) return ResponseEntity.badRequest().build();
        Student.Grade grade;
        try {
            grade = Student.Grade.valueOf(req.getGrade());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        Student student = Student.builder()
                .fullName(req.getFullName())
                .grade(grade)
                .dateOfBirth(req.getDateOfBirth())
                .parent(parent)
                .build();
        student = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(student));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentDto> update(@PathVariable String id, @RequestBody java.util.Map<String, Object> body) {
        return studentRepository.findById(id)
                .map(s -> {
                    if (body.containsKey("active")) s.setActive(Boolean.TRUE.equals(body.get("active")));
                    studentRepository.save(s);
                    return ResponseEntity.ok(toDto(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private StudentDto toDto(Student s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setFullName(s.getFullName());
        dto.setGrade(s.getGrade().name());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setParentId(s.getParent().getId());
        dto.setParentName(s.getParent().getFullName());
        dto.setActive(Boolean.TRUE.equals(s.getActive()));
        return dto;
    }
}
