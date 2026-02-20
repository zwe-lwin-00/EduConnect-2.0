package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlotDto {
    private String id;
    private int dayOfWeek;  // 1-7
    private LocalTime startTime;
    private LocalTime endTime;
}
