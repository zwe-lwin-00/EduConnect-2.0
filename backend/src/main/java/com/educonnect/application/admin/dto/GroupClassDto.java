package com.educonnect.application.admin.dto;

import java.time.LocalTime;
import java.util.Set;

public class GroupClassDto {
    private String id;
    private String name;
    private String teacherId;
    private String teacherName;
    private boolean active;
    private Set<Integer> daysOfWeek;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;
    private int enrollmentCount;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Set<Integer> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Set<Integer> daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public LocalTime getScheduleStartTime() { return scheduleStartTime; }
    public void setScheduleStartTime(LocalTime scheduleStartTime) { this.scheduleStartTime = scheduleStartTime; }
    public LocalTime getScheduleEndTime() { return scheduleEndTime; }
    public void setScheduleEndTime(LocalTime scheduleEndTime) { this.scheduleEndTime = scheduleEndTime; }
    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }
}
