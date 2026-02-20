package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherStudentDto;
import com.educonnect.domain.ContractSession;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import com.educonnect.domain.Student;

@RestController
@RequestMapping("/teacher/students")
@RequiredArgsConstructor
public class TeacherStudentsController {

    private final CurrentUserResolver currentUserResolver;
    private final ContractSessionRepository contractSessionRepository;

    @GetMapping
    public ResponseEntity<List<TeacherStudentDto>> list() {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<ContractSession> contracts = contractSessionRepository.findByTeacher_IdAndStatus(
                teacherId, ContractSession.ContractStatus.ACTIVE);
        List<TeacherStudentDto> list = contracts.stream()
                .map(ContractSession::getStudent)
                .collect(Collectors.toMap(Student::getId, s -> s, (a, b) -> a))
                .values().stream()
                .map(s -> new TeacherStudentDto(s.getId(), s.getFullName(), s.getGrade().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
