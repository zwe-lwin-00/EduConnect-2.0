package com.educonnect.web.parent;

import com.educonnect.application.parent.dto.ParentStudentDto;
import com.educonnect.repository.StudentRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parent/students")
@RequiredArgsConstructor
public class ParentStudentsController {

    private final CurrentUserResolver currentUserResolver;
    private final StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<List<ParentStudentDto>> list() {
        String parentId = currentUserResolver.getCurrentUserId();
        if (parentId == null) return ResponseEntity.status(401).build();
        List<ParentStudentDto> list = studentRepository.findByParent_Id(parentId).stream()
                .map(s -> new ParentStudentDto(s.getId(), s.getFullName(), s.getGrade().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
