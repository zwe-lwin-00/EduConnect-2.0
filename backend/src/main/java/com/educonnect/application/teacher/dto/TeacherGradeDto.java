package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherGradeDto {
    private String id;
    private String studentId;
    private String studentName;
    private String title;
    private Double gradeValue;
    private Double maxValue;
    private LocalDate gradeDate;
    private String notes;
}
