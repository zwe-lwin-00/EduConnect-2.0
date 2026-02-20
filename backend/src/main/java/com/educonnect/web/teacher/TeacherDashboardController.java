package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherDashboardDto;
import com.educonnect.repository.AttendanceLogRepository;
import com.educonnect.repository.GroupSessionRepository;
import com.educonnect.repository.HomeworkRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherDashboardController {

    private final CurrentUserResolver currentUserResolver;
    private final AttendanceLogRepository attendanceLogRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final HomeworkRepository homeworkRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<TeacherDashboardDto> dashboard() {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        LocalDate today = LocalDate.now();

        int todayOneToOne = attendanceLogRepository
                .findByContract_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, today, today).size();
        int todayGroup = groupSessionRepository
                .findByGroupClass_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, today, today).size();

        LocalDate weekEnd = today.plusWeeks(2);
        int upcomingOneToOne = attendanceLogRepository
                .findByContract_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, today.plusDays(1), weekEnd).size();
        int pendingHomework = homeworkRepository.findByTeacher_Id(teacherId).stream()
                .filter(h -> h.getStatus() == com.educonnect.domain.Homework.HomeworkStatus.SUBMITTED)
                .toList().size();

        TeacherDashboardDto dto = TeacherDashboardDto.builder()
                .todayOneToOneSessions(todayOneToOne)
                .todayGroupSessions(todayGroup)
                .upcomingOneToOneCount(upcomingOneToOne)
                .pendingHomeworkToGrade(pendingHomework)
                .build();
        return ResponseEntity.ok(dto);
    }
}
