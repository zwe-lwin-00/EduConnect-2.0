package com.educonnect.application.teacher;

import com.educonnect.application.teacher.dto.CalendarDayItemDto;
import com.educonnect.application.teacher.dto.TeacherCalendarResponseDto;
import com.educonnect.domain.*;
import com.educonnect.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherCalendarService {

    private final AttendanceLogRepository attendanceLogRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final ContractSessionRepository contractSessionRepository;
    private final GroupClassRepository groupClassRepository;
    private final HolidayRepository holidayRepository;

    public TeacherCalendarService(AttendanceLogRepository attendanceLogRepository,
                                  GroupSessionRepository groupSessionRepository,
                                  ContractSessionRepository contractSessionRepository,
                                  GroupClassRepository groupClassRepository,
                                  HolidayRepository holidayRepository) {
        this.attendanceLogRepository = attendanceLogRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.contractSessionRepository = contractSessionRepository;
        this.groupClassRepository = groupClassRepository;
        this.holidayRepository = holidayRepository;
    }

    public TeacherCalendarResponseDto getCalendar(String teacherId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        Set<String> holidaySet = holidayRepository.findByHolidayDateBetween(monthStart, monthEnd).stream()
                .map(h -> h.getHolidayDate().toString())
                .collect(Collectors.toSet());

        List<CalendarDayItemDto> items = new ArrayList<>();

        // Completed 1:1
        for (AttendanceLog log : attendanceLogRepository.findByContract_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, monthStart, monthEnd)) {
            items.add(CalendarDayItemDto.builder()
                    .dateYmd(log.getSessionDate().toString())
                    .type("ONE_TO_ONE")
                    .label(log.getContract().getStudent().getFullName())
                    .completed(true)
                    .id(log.getId())
                    .build());
        }

        // Completed group
        for (GroupSession gs : groupSessionRepository.findByGroupClass_Teacher_IdAndSessionDateBetweenOrderBySessionDateDesc(teacherId, monthStart, monthEnd)) {
            items.add(CalendarDayItemDto.builder()
                    .dateYmd(gs.getSessionDate().toString())
                    .type("GROUP")
                    .label("Group: " + gs.getGroupClass().getName())
                    .completed(true)
                    .id(gs.getId())
                    .build());
        }

        // Upcoming 1:1: contract schedule days in month with no attendance yet
        List<ContractSession> contracts = contractSessionRepository.findByTeacher_IdAndStatus(teacherId, ContractSession.ContractStatus.ACTIVE);
        for (ContractSession c : contracts) {
            if (c.getDaysOfWeek() == null) continue;
            for (LocalDate d = monthStart; !d.isAfter(monthEnd); d = d.plusDays(1)) {
                if (holidaySet.contains(d.toString())) continue;
                int dayOfWeek = d.getDayOfWeek().getValue(); // 1-7 Mon-Sun
                if (!c.getDaysOfWeek().contains(dayOfWeek)) continue;
                if (!attendanceLogRepository.findByContractIdAndSessionDate(c.getId(), d).isEmpty()) continue;
                items.add(CalendarDayItemDto.builder()
                        .dateYmd(d.toString())
                        .type("ONE_TO_ONE")
                        .label(c.getStudent().getFullName())
                        .completed(false)
                        .id(c.getId())
                        .build());
            }
        }

        // Upcoming group: group class schedule days in month with no session yet
        List<GroupClass> groupClasses = groupClassRepository.findByTeacher_IdAndActiveTrue(teacherId);
        for (GroupClass gc : groupClasses) {
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
