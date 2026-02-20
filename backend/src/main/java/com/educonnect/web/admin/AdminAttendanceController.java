package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.AttendanceDto;
import com.educonnect.domain.AttendanceLog;
import com.educonnect.repository.AttendanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
public class AdminAttendanceController {

    private final AttendanceLogRepository attendanceLogRepository;

    @GetMapping
    public List<AttendanceDto> listToday(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        return attendanceLogRepository.findBySessionDate(d).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AttendanceDto> override(
            @PathVariable String id,
            @RequestParam(required = false) Instant checkInAt,
            @RequestParam(required = false) Instant checkOutAt,
            @RequestParam(required = false) Double hoursUsed,
            @RequestParam(required = false) String lessonNotes) {
        return attendanceLogRepository.findById(id)
                .map(log -> {
                    if (checkInAt != null) log.setCheckInAt(checkInAt);
                    if (checkOutAt != null) log.setCheckOutAt(checkOutAt);
                    if (hoursUsed != null) log.setHoursUsed(hoursUsed);
                    if (lessonNotes != null) log.setLessonNotes(lessonNotes);
                    attendanceLogRepository.save(log);
                    return ResponseEntity.ok(toDto(log));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private AttendanceDto toDto(AttendanceLog log) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(log.getId());
        dto.setContractId(log.getContract().getId());
        dto.setTeacherName(log.getContract().getTeacher().getUser().getFullName());
        dto.setStudentName(log.getContract().getStudent().getFullName());
        dto.setSessionDate(log.getSessionDate());
        dto.setCheckInAt(log.getCheckInAt());
        dto.setCheckOutAt(log.getCheckOutAt());
        dto.setHoursUsed(log.getHoursUsed());
        dto.setLessonNotes(log.getLessonNotes());
        return dto;
    }
}
