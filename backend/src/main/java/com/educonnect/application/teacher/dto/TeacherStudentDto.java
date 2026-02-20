package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherStudentDto {
    private String id;
    private String fullName;
    private String grade;
}
