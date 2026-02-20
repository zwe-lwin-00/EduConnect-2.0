package com.educonnect.application.admin.usecase;

import com.educonnect.application.admin.dto.DashboardDto;
import com.educonnect.application.admin.port.DashboardQueryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GetDashboardUseCase {

    private final DashboardQueryPort dashboardQueryPort;

    public GetDashboardUseCase(DashboardQueryPort dashboardQueryPort) {
        this.dashboardQueryPort = dashboardQueryPort;
    }

    public DashboardDto execute(LocalDate date) {
        return dashboardQueryPort.getDashboard(date != null ? date : LocalDate.now());
    }
}
