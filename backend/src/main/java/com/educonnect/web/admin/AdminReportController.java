package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.ReportDto;
import com.educonnect.config.AppProperties;
import com.educonnect.domain.AttendanceLog;
import com.educonnect.repository.AttendanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AttendanceLogRepository attendanceLogRepository;
    private final AppProperties appProperties;

    @GetMapping("/daily")
    public List<ReportDto> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<AttendanceLog> logs = attendanceLogRepository.findBySessionDateBetween(from, to);
        Map<LocalDate, List<AttendanceLog>> byDate = logs.stream().collect(Collectors.groupingBy(AttendanceLog::getSessionDate));
        List<ReportDto> result = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            ReportDto r = new ReportDto();
            r.setDate(d);
            List<AttendanceLog> dayLogs = byDate.getOrDefault(d, List.of());
            r.setSessionCount(dayLogs.size());
            double revenue = 0;
            for (AttendanceLog log : dayLogs) {
                if (log.getHoursUsed() != null && log.getContract() != null && log.getContract().getTeacher() != null) {
                    Double rate = log.getContract().getTeacher().getHourlyRate();
                    if (rate == null) rate = appProperties.getDefaultHourlyRateForRevenue();
                    revenue += log.getHoursUsed() * rate;
                }
            }
            r.setRevenue(revenue);
            result.add(r);
        }
        return result;
    }

    @GetMapping("/monthly")
    public List<ReportDto> monthly(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (from == null) from = LocalDate.now().withDayOfMonth(1).minusMonths(11);
        if (to == null) to = LocalDate.now();
        LocalDate start = from.withDayOfMonth(1);
        List<ReportDto> result = new ArrayList<>();
        for (LocalDate month = start; !month.isAfter(to); month = month.plusMonths(1)) {
            LocalDate monthEnd = month.plusMonths(1).minusDays(1);
            LocalDate rangeStart = month.isBefore(from) ? from : month;
            LocalDate rangeEnd = monthEnd.isAfter(to) ? to : monthEnd;
            List<AttendanceLog> logs = attendanceLogRepository.findBySessionDateBetween(rangeStart, rangeEnd);
            ReportDto r = new ReportDto();
            r.setDate(month);
            r.setSessionCount(logs.size());
            double revenue = 0;
            for (AttendanceLog log : logs) {
                if (log.getHoursUsed() != null && log.getContract() != null && log.getContract().getTeacher() != null) {
                    Double rate = log.getContract().getTeacher().getHourlyRate();
                    if (rate == null) rate = appProperties.getDefaultHourlyRateForRevenue();
                    revenue += log.getHoursUsed() * rate;
                }
            }
            r.setRevenue(revenue);
            result.add(r);
        }
        return result;
    }
}
