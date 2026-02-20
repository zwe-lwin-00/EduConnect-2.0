package com.educonnect.application.teacher.dto;

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
public class TeacherSessionDto {
    public static final String TYPE_ONE_TO_ONE = "ONE_TO_ONE";
    public static final String TYPE_GROUP = "GROUP";

    private String id;
    private String type;  // ONE_TO_ONE | GROUP
    private LocalDate sessionDate;
    private String studentName;   // for 1:1
    private List<String> studentNames;  // for group
    private String groupClassName;  // for group
    private String contractId;  // for 1:1
    private String groupSessionId;  // for group
    private String groupClassId;
    private Instant checkInAt;
    private Instant checkOutAt;
    private Double hoursUsed;
    private String lessonNotes;
    private String zoomJoinUrl;
}
