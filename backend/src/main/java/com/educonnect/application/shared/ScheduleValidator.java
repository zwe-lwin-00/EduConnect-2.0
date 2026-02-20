package com.educonnect.application.shared;

import java.time.LocalTime;
import java.util.Set;

/**
 * Shared schedule validation for Group classes and One-To-One contracts:
 * days of week 1–7 (no duplicates), and end time after start time when both present.
 */
public final class ScheduleValidator {

    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 7;

    private ScheduleValidator() {}

    /**
     * Validates days of week: each value must be in 1–7. Set implies no duplicates.
     * @param daysOfWeek can be null or empty (no validation)
     * @throws IllegalArgumentException if any value is not in [1, 7]
     */
    public static void validateDaysOfWeek(Set<Integer> daysOfWeek) {
        if (daysOfWeek == null) return;
        for (Integer d : daysOfWeek) {
            if (d == null || d < MIN_DAY || d > MAX_DAY) {
                throw new IllegalArgumentException(
                    "Days of week must be between " + MIN_DAY + " and " + MAX_DAY + " (no duplicates). Invalid: " + d);
            }
        }
    }

    /**
     * Validates that end time is after start time when both are non-null.
     * @throws IllegalArgumentException if both are non-null and end is not after start
     */
    public static void validateScheduleTimes(LocalTime scheduleStartTime, LocalTime scheduleEndTime) {
        if (scheduleStartTime == null || scheduleEndTime == null) return;
        if (!scheduleEndTime.isAfter(scheduleStartTime)) {
            throw new IllegalArgumentException("Schedule end time must be after start time.");
        }
    }
}
