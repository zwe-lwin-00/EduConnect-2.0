package com.educonnect.web.parent;

import com.educonnect.application.parent.dto.StudentOverviewDto;
import com.educonnect.domain.*;
import com.educonnect.repository.*;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parent/students")
@RequiredArgsConstructor
public class ParentStudentOverviewController {

    private final CurrentUserResolver currentUserResolver;
    private final StudentRepository studentRepository;
    private final ContractSessionRepository contractSessionRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final HomeworkRepository homeworkRepository;
    private final StudentGradeRepository gradeRepository;
    private final GroupClassEnrollmentRepository enrollmentRepository;
    private final GroupSessionRepository groupSessionRepository;

    @GetMapping("/{studentId}/overview")
    public ResponseEntity<StudentOverviewDto> overview(@PathVariable String studentId) {
        String parentId = currentUserResolver.getCurrentUserId();
        if (parentId == null) return ResponseEntity.status(401).build();
        var student = studentRepository.findById(studentId).orElse(null);
        if (student == null || !student.getParent().getId().equals(parentId)) {
            return ResponseEntity.notFound().build();
        }

        String assignedTeacherName = null;
        var contracts = contractSessionRepository.findByStudent_IdAndStatus(studentId, ContractSession.ContractStatus.ACTIVE);
        if (!contracts.isEmpty()) {
            assignedTeacherName = contracts.get(0).getTeacher().getUser().getFullName();
        }

        LocalDate from = LocalDate.now().minusMonths(3);
        LocalDate to = LocalDate.now();
        List<StudentOverviewDto.SessionSummaryDto> recentSessions = new ArrayList<>();
        for (AttendanceLog log : attendanceLogRepository.findByContract_Student_IdAndSessionDateBetweenOrderBySessionDateDesc(studentId, from, to)) {
            recentSessions.add(new StudentOverviewDto.SessionSummaryDto(
                    log.getSessionDate(),
                    "ONE_TO_ONE",
                    log.getContract().getTeacher().getUser().getFullName(),
                    log.getCheckInAt(),
                    log.getCheckOutAt(),
                    log.getLessonNotes()
            ));
        }
        for (var en : enrollmentRepository.findByStudent_Id(studentId)) {
            for (GroupSession gs : groupSessionRepository.findByGroupClass_IdAndSessionDateBetweenOrderBySessionDateDesc(en.getGroupClass().getId(), from, to)) {
                recentSessions.add(new StudentOverviewDto.SessionSummaryDto(
                        gs.getSessionDate(),
                        "GROUP",
                        gs.getGroupClass().getName(),
                        gs.getCheckInAt(),
                        gs.getCheckOutAt(),
                        gs.getLessonNotes()
                ));
            }
        }
        recentSessions.sort((a, b) -> (b.getSessionDate() != null && a.getSessionDate() != null)
                ? b.getSessionDate().compareTo(a.getSessionDate()) : 0);
        if (recentSessions.size() > 20) recentSessions = recentSessions.subList(0, 20);

        List<StudentOverviewDto.HomeworkSummaryDto> homework = homeworkRepository.findByStudent_Id(studentId).stream()
                .map(h -> new StudentOverviewDto.HomeworkSummaryDto(
                        h.getId(), h.getTitle(), h.getDueDate(), h.getStatus().name(), h.getTeacherFeedback()))
                .collect(Collectors.toList());

        List<StudentOverviewDto.GradeSummaryDto> grades = gradeRepository.findByStudent_Id(studentId).stream()
                .map(g -> new StudentOverviewDto.GradeSummaryDto(
                        g.getId(), g.getTitle(), g.getGradeValue(), g.getMaxValue(), g.getGradeDate(), g.getNotes()))
                .collect(Collectors.toList());

        int totalSessions = attendanceLogRepository.findByContract_Student_Id(studentId).size();
        for (var en : enrollmentRepository.findByStudent_Id(studentId)) {
            totalSessions += groupSessionRepository.findByGroupClass_IdAndSessionDateBetweenOrderBySessionDateDesc(
                    en.getGroupClass().getId(), LocalDate.now().minusYears(1), LocalDate.now()).size();
        }
        int completedHomework = (int) homework.stream().filter(h -> "GRADED".equals(h.getStatus()) || "SUBMITTED".equals(h.getStatus())).count();

        StudentOverviewDto dto = StudentOverviewDto.builder()
                .studentId(student.getId())
                .studentName(student.getFullName())
                .grade(student.getGrade().name())
                .assignedTeacherName(assignedTeacherName)
                .recentSessions(recentSessions)
                .homework(homework)
                .grades(grades)
                .totalSessionsCount(totalSessions)
                .completedHomeworkCount(completedHomework)
                .build();
        return ResponseEntity.ok(dto);
    }
}
