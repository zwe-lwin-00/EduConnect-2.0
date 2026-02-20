package com.educonnect.application.teacher.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlotDto {
    private String id;
    @Min(1) @Max(7)
    private int dayOfWeek;  // 1-7
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
}
