package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherHomeworkDto {
    private String id;
    private String studentId;
    private String studentName;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;  // ASSIGNED, SUBMITTED, GRADED, OVERDUE
    private String teacherFeedback;
}
