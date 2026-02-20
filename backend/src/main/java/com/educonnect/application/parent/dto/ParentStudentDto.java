package com.educonnect.application.parent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentStudentDto {
    private String id;
    private String fullName;
    private String grade;
}
