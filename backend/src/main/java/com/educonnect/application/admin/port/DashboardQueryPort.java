package com.educonnect.application.admin.port;

import com.educonnect.application.admin.dto.DashboardDto;

import java.time.LocalDate;

/**
 * Port for loading admin dashboard data. Implemented by persistence adapter.
 */
public interface DashboardQueryPort {

    DashboardDto getDashboard(LocalDate date);
}
