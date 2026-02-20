package com.educonnect.web.admin;

import com.educonnect.application.admin.dto.DashboardDto;
import com.educonnect.application.admin.usecase.GetDashboardUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Admin feature: dashboard endpoint.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final GetDashboardUseCase getDashboardUseCase;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(getDashboardUseCase.execute(date));
    }
}
