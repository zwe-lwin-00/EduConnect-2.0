package com.educonnect.application.admin.dto;

import java.time.LocalDate;

public class HolidayDto {
    private String id;
    private LocalDate holidayDate;
    private String name;
    private String description;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public void setHolidayDate(LocalDate holidayDate) { this.holidayDate = holidayDate; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
