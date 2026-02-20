package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileDto {
    private String id;
    private String email;
    private String fullName;
    private String phone;
    private String education;
    private String bio;
    private Set<String> specializations;
    private String verificationStatus;
    private String zoomJoinUrl;  // for One-To-One join URL
}
