package com.educonnect.application.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class CreateContractRequest {
    @NotBlank
    private String teacherId;
    @NotBlank
    private String studentId;
    private String subscriptionId;   // if null, use legacy
    private LocalDate legacyPeriodEnd;
    private Set<Integer> daysOfWeek; // 1-7
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
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
}
