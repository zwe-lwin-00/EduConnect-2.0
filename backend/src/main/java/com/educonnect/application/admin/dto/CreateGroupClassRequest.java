package com.educonnect.application.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.Set;

public class CreateGroupClassRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String teacherId;
    private Set<Integer> daysOfWeek;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public Set<Integer> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Set<Integer> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public LocalTime getScheduleStartTime() { return scheduleStartTime; }
    public void setScheduleStartTime(LocalTime scheduleStartTime) { this.scheduleStartTime = scheduleStartTime; }
    public LocalTime getScheduleEndTime() { return scheduleEndTime; }
    public void setScheduleEndTime(LocalTime scheduleEndTime) { this.scheduleEndTime = scheduleEndTime; }
}
