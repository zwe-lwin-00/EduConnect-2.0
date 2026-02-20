package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.AvailabilitySlotDto;
import com.educonnect.domain.TeacherAvailability;
import com.educonnect.repository.TeacherAvailabilityRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/availability")
@RequiredArgsConstructor
public class TeacherAvailabilityController {

    private final CurrentUserResolver currentUserResolver;
    private final TeacherAvailabilityRepository availabilityRepository;

    @GetMapping
    public ResponseEntity<List<AvailabilitySlotDto>> get() {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        List<AvailabilitySlotDto> list = availabilityRepository.findByTeacher_Id(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping
    public ResponseEntity<List<AvailabilitySlotDto>> put(@RequestBody List<AvailabilitySlotDto> slots) {
        var teacher = currentUserResolver.requireCurrentTeacher();
        String teacherId = teacher.getId();
        availabilityRepository.findByTeacher_Id(teacherId).forEach(availabilityRepository::delete);
        for (AvailabilitySlotDto dto : slots) {
            TeacherAvailability a = TeacherAvailability.builder()
                    .teacher(teacher)
                    .dayOfWeek(dto.getDayOfWeek())
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .build();
            availabilityRepository.save(a);
        }
        return get();
    }

    private AvailabilitySlotDto toDto(TeacherAvailability a) {
        return new AvailabilitySlotDto(a.getId(), a.getDayOfWeek(), a.getStartTime(), a.getEndTime());
    }
}
