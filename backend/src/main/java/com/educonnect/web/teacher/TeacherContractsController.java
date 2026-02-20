package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherContractDto;
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

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherContractsController {

    private final CurrentUserResolver currentUserResolver;
    private final ContractSessionRepository contractSessionRepository;

    @GetMapping("/contracts")
    public ResponseEntity<List<TeacherContractDto>> list() {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<TeacherContractDto> list = contractSessionRepository.findByTeacher_IdAndStatus(teacherId, ContractSession.ContractStatus.ACTIVE)
                .stream()
                .map(c -> new TeacherContractDto(c.getId(), c.getStudent().getId(), c.getStudent().getFullName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
