package com.educonnect.application.admin.dto;

import java.time.LocalDate;

public class ReportDto {
    private LocalDate date;
    private long sessionCount;
    private double revenue;

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public long getSessionCount() { return sessionCount; }
    public void setSessionCount(long sessionCount) { this.sessionCount = sessionCount; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
}
