package com.educonnect.application.admin.dto;

import java.util.List;

/**
 * Dashboard data for admin home. API contract for GET /admin/dashboard.
 */
public class DashboardDto {

    private long pendingTeacherVerifications;
    private long todaySessionsCount;
    private long subscriptionsExpiringSoon;
    private long contractsExpiringSoon;
    private double revenueThisMonth;       // from sessions + teacher/default rate
    private List<String> pendingActions;   // e.g. "3 teachers pending verification"
    private List<?> alerts;

    public long getPendingTeacherVerifications() { return pendingTeacherVerifications; }
    public void setPendingTeacherVerifications(long pendingTeacherVerifications) { this.pendingTeacherVerifications = pendingTeacherVerifications; }
    public long getTodaySessionsCount() { return todaySessionsCount; }
    public void setTodaySessionsCount(long todaySessionsCount) { this.todaySessionsCount = todaySessionsCount; }
    public long getSubscriptionsExpiringSoon() { return subscriptionsExpiringSoon; }
    public void setSubscriptionsExpiringSoon(long subscriptionsExpiringSoon) { this.subscriptionsExpiringSoon = subscriptionsExpiringSoon; }
    public long getContractsExpiringSoon() { return contractsExpiringSoon; }
    public void setContractsExpiringSoon(long contractsExpiringSoon) { this.contractsExpiringSoon = contractsExpiringSoon; }
    public double getRevenueThisMonth() { return revenueThisMonth; }
    public void setRevenueThisMonth(double revenueThisMonth) { this.revenueThisMonth = revenueThisMonth; }
    public List<String> getPendingActions() { return pendingActions; }
    public void setPendingActions(List<String> pendingActions) { this.pendingActions = pendingActions; }
    public List<?> getAlerts() { return alerts; }
    public void setAlerts(List<?> alerts) { this.alerts = alerts; }
}
