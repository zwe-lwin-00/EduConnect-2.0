package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherGroupClassDto {
    private String id;
    private String name;
    private String zoomJoinUrl;
    private boolean active;
    private Set<Integer> daysOfWeek;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;
    private int enrollmentCount;
}
