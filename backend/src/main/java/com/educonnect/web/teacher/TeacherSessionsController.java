package com.educonnect.web.teacher;

import com.educonnect.application.teacher.dto.TeacherSessionDto;
import com.educonnect.domain.AttendanceLog;
import com.educonnect.domain.ContractSession;
import com.educonnect.domain.GroupSession;
import com.educonnect.repository.AttendanceLogRepository;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.repository.GroupSessionRepository;
import com.educonnect.web.common.CurrentUserResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher/sessions")
@RequiredArgsConstructor
public class TeacherSessionsController {

    private final CurrentUserResolver currentUserResolver;
    private final AttendanceLogRepository attendanceLogRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final ContractSessionRepository contractSessionRepository;

    @GetMapping
    public ResponseEntity<List<TeacherSessionDto>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusWeeks(1);

        List<TeacherSessionDto> list = new ArrayList<>();
        for (AttendanceLog log : attendanceLogRepository.findByContract_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, start, end)) {
            list.add(TeacherSessionDto.builder()
                    .id(log.getId())
                    .type(TeacherSessionDto.TYPE_ONE_TO_ONE)
                    .sessionDate(log.getSessionDate())
                    .studentName(log.getContract().getStudent().getFullName())
                    .contractId(log.getContract().getId())
                    .checkInAt(log.getCheckInAt())
                    .checkOutAt(log.getCheckOutAt())
                    .hoursUsed(log.getHoursUsed())
                    .lessonNotes(log.getLessonNotes())
                    .zoomJoinUrl(log.getZoomJoinUrl() != null ? log.getZoomJoinUrl() : log.getContract().getTeacher().getZoomJoinUrl())
                    .build());
        }
        for (GroupSession gs : groupSessionRepository.findByGroupClass_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, start, end)) {
            List<String> studentNames = gs.getAttendances().stream()
                    .map(a -> a.getStudent().getFullName())
                    .collect(Collectors.toList());
            list.add(TeacherSessionDto.builder()
                    .id(gs.getId())
                    .type(TeacherSessionDto.TYPE_GROUP)
                    .sessionDate(gs.getSessionDate())
                    .studentNames(studentNames)
                    .groupClassName(gs.getGroupClass().getName())
                    .groupSessionId(gs.getId())
                    .groupClassId(gs.getGroupClass().getId())
                    .checkInAt(gs.getCheckInAt())
                    .checkOutAt(gs.getCheckOutAt())
                    .lessonNotes(gs.getLessonNotes())
                    .zoomJoinUrl(gs.getZoomJoinUrl() != null ? gs.getZoomJoinUrl() : gs.getGroupClass().getZoomJoinUrl())
                    .build());
        }
        list.sort((a, b) -> {
            int d = (a.getSessionDate() != null && b.getSessionDate() != null)
                    ? b.getSessionDate().compareTo(a.getSessionDate()) : 0;
            return d != 0 ? d : (a.getSessionDate() == null ? 1 : (b.getSessionDate() == null ? -1 : 0));
        });
        return ResponseEntity.ok(list);
    }

    @PostMapping("/attendance")
    public ResponseEntity<TeacherSessionDto> createAttendance(
            @RequestParam String contractId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sessionDate) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        ContractSession contract = contractSessionRepository.findById(contractId).orElse(null);
        if (contract == null || !contract.getTeacher().getId().equals(teacherId)
                || contract.getStatus() != ContractSession.ContractStatus.ACTIVE) {
            return ResponseEntity.notFound().build();
        }
        LocalDate date = sessionDate != null ? sessionDate : LocalDate.now();
        List<AttendanceLog> existing = attendanceLogRepository.findByContractIdAndSessionDate(contractId, date);
        if (!existing.isEmpty()) {
            AttendanceLog log = existing.get(0);
            return ResponseEntity.ok(toOneToOneDto(log));
        }
        AttendanceLog log = AttendanceLog.builder()
                .contract(contract)
                .sessionDate(date)
                .zoomJoinUrl(contract.getTeacher().getZoomJoinUrl())
                .build();
        log = attendanceLogRepository.save(log);
        return ResponseEntity.ok(toOneToOneDto(log));
    }

    private TeacherSessionDto toOneToOneDto(AttendanceLog log) {
        return TeacherSessionDto.builder()
                .id(log.getId())
                .type(TeacherSessionDto.TYPE_ONE_TO_ONE)
                .sessionDate(log.getSessionDate())
                .studentName(log.getContract().getStudent().getFullName())
                .contractId(log.getContract().getId())
                .checkInAt(log.getCheckInAt())
                .checkOutAt(log.getCheckOutAt())
                .hoursUsed(log.getHoursUsed())
                .lessonNotes(log.getLessonNotes())
                .zoomJoinUrl(log.getZoomJoinUrl() != null ? log.getZoomJoinUrl() : log.getContract().getTeacher().getZoomJoinUrl())
                .build();
    }

    @PatchMapping("/attendance/{id}")
    public ResponseEntity<TeacherSessionDto> updateAttendance(
            @PathVariable String id,
            @RequestParam(required = false) Instant checkInAt,
            @RequestParam(required = false) Instant checkOutAt,
            @RequestParam(required = false) Double hoursUsed,
            @RequestParam(required = false) String lessonNotes) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        AttendanceLog log = attendanceLogRepository.findById(id).orElse(null);
        if (log == null || !log.getContract().getTeacher().getId().equals(teacherId)) {
            return ResponseEntity.notFound().build();
        }
        if (checkInAt != null) log.setCheckInAt(checkInAt);
        if (checkOutAt != null) log.setCheckOutAt(checkOutAt);
        if (hoursUsed != null) log.setHoursUsed(hoursUsed);
        if (lessonNotes != null) log.setLessonNotes(lessonNotes);
        attendanceLogRepository.save(log);
        return ResponseEntity.ok(toOneToOneDto(log));
    }

    @PatchMapping("/group/{id}")
    public ResponseEntity<TeacherSessionDto> updateGroupSession(
            @PathVariable String id,
            @RequestParam(required = false) Instant checkInAt,
            @RequestParam(required = false) Instant checkOutAt,
            @RequestParam(required = false) String lessonNotes) {
        String teacherId = currentUserResolver.requireCurrentTeacher().getId();
        GroupSession gs = groupSessionRepository.findById(id).orElse(null);
        if (gs == null || !gs.getGroupClass().getTeacher().getId().equals(teacherId)) {
            return ResponseEntity.notFound().build();
        }
        if (checkInAt != null) gs.setCheckInAt(checkInAt);
        if (checkOutAt != null) gs.setCheckOutAt(checkOutAt);
        if (lessonNotes != null) gs.setLessonNotes(lessonNotes);
        groupSessionRepository.save(gs);
        List<String> studentNames = gs.getAttendances().stream()
                .map(a -> a.getStudent().getFullName())
                .collect(Collectors.toList());
        TeacherSessionDto dto = TeacherSessionDto.builder()
                .id(gs.getId())
                .type(TeacherSessionDto.TYPE_GROUP)
                .sessionDate(gs.getSessionDate())
                .studentNames(studentNames)
                .groupClassName(gs.getGroupClass().getName())
                .groupSessionId(gs.getId())
                .groupClassId(gs.getGroupClass().getId())
                .checkInAt(gs.getCheckInAt())
                .checkOutAt(gs.getCheckOutAt())
                .lessonNotes(gs.getLessonNotes())
                .zoomJoinUrl(gs.getZoomJoinUrl())
                .build();
        return ResponseEntity.ok(dto);
    }
}
