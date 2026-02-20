package com.educonnect.web.teacher;

import com.educonnect.application.teacher.TeacherCalendarService;
import com.educonnect.application.teacher.dto.TeacherCalendarResponseDto;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher/calendar")
@RequiredArgsConstructor
public class TeacherCalendarController {

    private final CurrentUserResolver currentUserResolver;
    private final TeacherCalendarService teacherCalendarService;

    @GetMapping
    public ResponseEntity<TeacherCalendarResponseDto> getCalendar(
            @RequestParam int year,
            @RequestParam int month) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        return ResponseEntity.ok(teacherCalendarService.getCalendar(teacherId, year, month));
    }
}
