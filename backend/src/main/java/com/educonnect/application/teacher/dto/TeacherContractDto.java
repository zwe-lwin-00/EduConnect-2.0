package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherContractDto {
    private String id;
    private String studentId;
    private String studentName;
}
