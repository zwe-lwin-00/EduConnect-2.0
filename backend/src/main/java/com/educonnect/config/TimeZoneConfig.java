package com.educonnect.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * Sets the application default time zone (e.g. Asia/Yangon for Myanmar).
 * "Today" and report date ranges use this; API continues to serialize Instant as UTC (Z).
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class TimeZoneConfig {

    private String timezone; // from app.timezone in application.yml

    @PostConstruct
    public void init() {
        String tz = (timezone != null && !timezone.isBlank()) ? timezone : "UTC";
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
