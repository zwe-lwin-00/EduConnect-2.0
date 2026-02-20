package com.educonnect.application.parent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentOverviewDto {
    private String studentId;
    private String studentName;
    private String grade;
    private String assignedTeacherName;  // primary 1:1 teacher if any
    private List<SessionSummaryDto> recentSessions;
    private List<HomeworkSummaryDto> homework;
    private List<GradeSummaryDto> grades;
    private int totalSessionsCount;
    private int completedHomeworkCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionSummaryDto {
        private LocalDate sessionDate;
        private String type;  // ONE_TO_ONE | GROUP
        private String teacherOrClassName;
        private Instant checkInAt;
        private Instant checkOutAt;
        private String lessonNotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeworkSummaryDto {
        private String id;
        private String title;
        private LocalDate dueDate;
        private String status;
        private String teacherFeedback;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GradeSummaryDto {
        private String id;
        private String title;
        private Double gradeValue;
        private Double maxValue;
        private LocalDate gradeDate;
        private String notes;
    }
}
