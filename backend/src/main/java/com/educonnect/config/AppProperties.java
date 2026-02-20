package com.educonnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private int subscriptionExpiringAlertDays = 7;
    private int contractExpiringAlertDays = 14;
    private int contractEndingSoonNotificationDays = 14;
    private double defaultHourlyRateForRevenue = 0;
    private double groupSessionMinHoursPerStudent = 0.25;
    private int passwordGenerationLength = 12;
    private int parentOverviewRecentMonths = 3;
    private int parentOverviewRecentSessionsMax = 20;
    private int parentOverviewTotalSessionsYears = 1;
    private int reportDefaultMonthsBack = 11;
    private String apiPublicUrl = "http://localhost:9099";

    public int getSubscriptionExpiringAlertDays() {
        return subscriptionExpiringAlertDays;
    }

    public void setSubscriptionExpiringAlertDays(int subscriptionExpiringAlertDays) {
        this.subscriptionExpiringAlertDays = subscriptionExpiringAlertDays;
    }

    public int getContractExpiringAlertDays() {
        return contractExpiringAlertDays;
    }

    public void setContractExpiringAlertDays(int contractExpiringAlertDays) {
        this.contractExpiringAlertDays = contractExpiringAlertDays;
    }

    public int getContractEndingSoonNotificationDays() {
        return contractEndingSoonNotificationDays;
    }

    public void setContractEndingSoonNotificationDays(int contractEndingSoonNotificationDays) {
        this.contractEndingSoonNotificationDays = contractEndingSoonNotificationDays;
    }

    public double getDefaultHourlyRateForRevenue() {
        return defaultHourlyRateForRevenue;
    }

    public void setDefaultHourlyRateForRevenue(double defaultHourlyRateForRevenue) {
        this.defaultHourlyRateForRevenue = defaultHourlyRateForRevenue;
    }

    public double getGroupSessionMinHoursPerStudent() {
        return groupSessionMinHoursPerStudent;
    }

    public void setGroupSessionMinHoursPerStudent(double groupSessionMinHoursPerStudent) {
        this.groupSessionMinHoursPerStudent = groupSessionMinHoursPerStudent;
    }

    public int getPasswordGenerationLength() {
        return passwordGenerationLength;
    }

    public void setPasswordGenerationLength(int passwordGenerationLength) {
        this.passwordGenerationLength = passwordGenerationLength;
    }

    public int getParentOverviewRecentMonths() {
        return parentOverviewRecentMonths;
    }

    public void setParentOverviewRecentMonths(int parentOverviewRecentMonths) {
        this.parentOverviewRecentMonths = parentOverviewRecentMonths;
    }

    public int getParentOverviewRecentSessionsMax() {
        return parentOverviewRecentSessionsMax;
    }

    public void setParentOverviewRecentSessionsMax(int parentOverviewRecentSessionsMax) {
        this.parentOverviewRecentSessionsMax = parentOverviewRecentSessionsMax;
    }

    public int getParentOverviewTotalSessionsYears() {
        return parentOverviewTotalSessionsYears;
    }

    public void setParentOverviewTotalSessionsYears(int parentOverviewTotalSessionsYears) {
        this.parentOverviewTotalSessionsYears = parentOverviewTotalSessionsYears;
    }

    public int getReportDefaultMonthsBack() {
        return reportDefaultMonthsBack;
    }

    public void setReportDefaultMonthsBack(int reportDefaultMonthsBack) {
        this.reportDefaultMonthsBack = reportDefaultMonthsBack;
    }

    public String getApiPublicUrl() {
        return apiPublicUrl;
    }

    public void setApiPublicUrl(String apiPublicUrl) {
        this.apiPublicUrl = apiPublicUrl;
    }
}
