package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherCalendarResponseDto {
    private List<CalendarDayItemDto> items;
    /** Holiday dates in the month as yyyy-MM-dd for day matching. */
    private List<String> holidays;
}
