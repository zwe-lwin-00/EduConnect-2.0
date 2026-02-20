package com.educonnect.application.parent;

import com.educonnect.application.teacher.dto.CalendarDayItemDto;
import com.educonnect.application.teacher.dto.TeacherCalendarResponseDto;
import com.educonnect.domain.*;
import com.educonnect.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParentStudentCalendarService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final GroupSessionAttendanceRepository groupSessionAttendanceRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final ContractSessionRepository contractSessionRepository;
    private final GroupClassEnrollmentRepository enrollmentRepository;
    private final HolidayRepository holidayRepository;

    public ParentStudentCalendarService(AttendanceLogRepository attendanceLogRepository,
                                        GroupSessionAttendanceRepository groupSessionAttendanceRepository,
                                        GroupSessionRepository groupSessionRepository,
                                        ContractSessionRepository contractSessionRepository,
                                        GroupClassEnrollmentRepository enrollmentRepository,
                                        HolidayRepository holidayRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
        this.groupSessionAttendanceRepository = groupSessionAttendanceRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.contractSessionRepository = contractSessionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.holidayRepository = holidayRepository;
    }

    /**
     * Builds calendar for one student. Caller must ensure student belongs to the current parent.
     */
    public TeacherCalendarResponseDto getCalendar(String studentId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        Set<String> holidaySet = holidayRepository.findByHolidayDateBetween(monthStart, monthEnd).stream()
                .map(h -> h.getHolidayDate().toString())
                .collect(Collectors.toSet());

        List<CalendarDayItemDto> items = new ArrayList<>();

        // Completed 1:1
        for (AttendanceLog log : attendanceLogRepository.findByContract_Student_IdAndSessionDateBetweenOrderBySessionDateDesc(studentId, monthStart, monthEnd)) {
            items.add(CalendarDayItemDto.builder()
                    .dateYmd(log.getSessionDate().toString())
                    .type("ONE_TO_ONE")
                    .label(log.getContract().getTeacher().getUser().getFullName())
                    .completed(true)
                    .id(log.getId())
                    .build());
        }

        // Completed group: only sessions this student attended (has GroupSessionAttendance)
        for (GroupSessionAttendance att : groupSessionAttendanceRepository.findByStudent_IdAndGroupSession_SessionDateBetweenOrderByGroupSession_SessionDateDesc(studentId, monthStart, monthEnd)) {
            GroupSession gs = att.getGroupSession();
            items.add(CalendarDayItemDto.builder()
                    .dateYmd(gs.getSessionDate().toString())
                    .type("GROUP")
                    .label("Group: " + gs.getGroupClass().getName())
                    .completed(true)
                    .id(gs.getId())
                    .build());
        }

        // Upcoming 1:1: student's ACTIVE contracts, schedule days in month with no attendance, not holiday
        List<ContractSession> contracts = contractSessionRepository.findByStudent_IdAndStatus(studentId, ContractSession.ContractStatus.ACTIVE);
        for (ContractSession c : contracts) {
            if (c.getDaysOfWeek() == null) continue;
            for (LocalDate d = monthStart; !d.isAfter(monthEnd); d = d.plusDays(1)) {
                if (holidaySet.contains(d.toString())) continue;
                int dayOfWeek = d.getDayOfWeek().getValue();
                if (!c.getDaysOfWeek().contains(dayOfWeek)) continue;
                if (!attendanceLogRepository.findByContractIdAndSessionDate(c.getId(), d).isEmpty()) continue;
                items.add(CalendarDayItemDto.builder()
                        .dateYmd(d.toString())
                        .type("ONE_TO_ONE")
                        .label(c.getTeacher().getUser().getFullName())
                        .completed(false)
                        .id(c.getId())
                        .build());
            }
        }

        // Upcoming group: student's enrolled classes, schedule days in month with no session yet, not holiday
        for (GroupClassEnrollment en : enrollmentRepository.findByStudent_Id(studentId)) {
            GroupClass gc = en.getGroupClass();
            if (gc.getDaysOfWeek() == null) continue;
            for (LocalDate d = monthStart; !d.isAfter(monthEnd); d = d.plusDays(1)) {
                if (holidaySet.contains(d.toString())) continue;
                int dayOfWeek = d.getDayOfWeek().getValue();
                if (!gc.getDaysOfWeek().contains(dayOfWeek)) continue;
                if (!groupSessionRepository.findByGroupClass_IdAndSessionDateBetweenOrderBySessionDateDesc(gc.getId(), d, d).isEmpty()) continue;
                items.add(CalendarDayItemDto.builder()
                        .dateYmd(d.toString())
                        .type("GROUP")
                        .label("Group: " + gc.getName())
                        .completed(false)
                        .id(gc.getId())
                        .build());
            }
        }

        List<String> holidays = new ArrayList<>(holidaySet);
        Collections.sort(holidays);
        return TeacherCalendarResponseDto.builder().items(items).holidays(holidays).build();
    }
}
