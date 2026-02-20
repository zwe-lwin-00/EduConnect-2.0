package com.educonnect.application.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One calendar entry for a day. dateYmd is "yyyy-MM-dd" for correct day matching and holidays.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDayItemDto {
    /** Date as yyyy-MM-dd (DateYmd) for day matching and holiday checks. */
    private String dateYmd;
    /** ONE_TO_ONE or GROUP */
    private String type;
    /** e.g. student name, or "Group: &lt;class name&gt;" */
    private String label;
    private boolean completed;
    /** Optional: attendance log id or group session id for linking. */
    private String id;
}
