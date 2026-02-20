package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDashboardDto {
    private int todayOneToOneSessions;
    private int todayGroupSessions;
    private int upcomingOneToOneCount;
    private int pendingHomeworkToGrade;
}
