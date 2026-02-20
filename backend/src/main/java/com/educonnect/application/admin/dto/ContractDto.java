package com.educonnect.application.admin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ContractDto {
    private String id;
    private String teacherId;
    private String teacherName;
    private String studentId;
    private String studentName;
    private String subscriptionId;
    private LocalDate legacyPeriodEnd;
    private Set<Integer> daysOfWeek;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;
    private String status; // ACTIVE, CANCELLED, ENDED

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
    public LocalDate getLegacyPeriodEnd() { return legacyPeriodEnd; }
    public void setLegacyPeriodEnd(LocalDate legacyPeriodEnd) { this.legacyPeriodEnd = legacyPeriodEnd; }
    public Set<Integer> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Set<Integer> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public LocalTime getScheduleStartTime() { return scheduleStartTime; }
    public void setScheduleStartTime(LocalTime scheduleStartTime) { this.scheduleStartTime = scheduleStartTime; }
    public LocalTime getScheduleEndTime() { return scheduleEndTime; }
    public void setScheduleEndTime(LocalTime scheduleEndTime) { this.scheduleEndTime = scheduleEndTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
