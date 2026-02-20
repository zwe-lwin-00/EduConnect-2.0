package com.educonnect.application.admin.dto;

import java.time.Instant;
import java.time.LocalDate;

public class AttendanceDto {
    private String id;
    private String contractId;
    private String teacherName;
    private String studentName;
    private LocalDate sessionDate;
    private Instant checkInAt;
    private Instant checkOutAt;
    private Double hoursUsed;
    private String lessonNotes;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContractId() { return contractId; }
    public void setContractId(String contractId) { this.contractId = contractId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public Instant getCheckInAt() { return checkInAt; }
    public void setCheckInAt(Instant checkInAt) { this.checkInAt = checkInAt; }
    public Instant getCheckOutAt() { return checkOutAt; }
    public void setCheckOutAt(Instant checkOutAt) { this.checkOutAt = checkOutAt; }
    public Double getHoursUsed() { return hoursUsed; }
    public void setHoursUsed(Double hoursUsed) { this.hoursUsed = hoursUsed; }
    public String getLessonNotes() { return lessonNotes; }
    public void setLessonNotes(String lessonNotes) { this.lessonNotes = lessonNotes; }
}
