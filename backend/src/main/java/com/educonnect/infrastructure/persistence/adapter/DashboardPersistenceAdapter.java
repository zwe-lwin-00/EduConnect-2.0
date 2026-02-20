package com.educonnect.infrastructure.persistence.adapter;

import com.educonnect.application.admin.dto.DashboardDto;
import com.educonnect.application.admin.port.DashboardQueryPort;
import com.educonnect.config.AppProperties;
import com.educonnect.domain.AttendanceLog;
import com.educonnect.domain.ContractSession;
import com.educonnect.domain.Subscription;
import com.educonnect.domain.TeacherProfile;
import com.educonnect.repository.AttendanceLogRepository;
import com.educonnect.repository.ContractSessionRepository;
import com.educonnect.repository.SubscriptionRepository;
import com.educonnect.repository.TeacherProfileRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DashboardPersistenceAdapter implements DashboardQueryPort {

    private final ContractSessionRepository contractRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AttendanceLogRepository attendanceLogRepository;
    private final AppProperties appProperties;

    public DashboardPersistenceAdapter(ContractSessionRepository contractRepository,
                                       SubscriptionRepository subscriptionRepository,
                                       TeacherProfileRepository teacherProfileRepository,
                                       AttendanceLogRepository attendanceLogRepository,
                                       AppProperties appProperties) {
        this.contractRepository = contractRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.attendanceLogRepository = attendanceLogRepository;
        this.appProperties = appProperties;
    }

    @Override
    public DashboardDto getDashboard(LocalDate date) {
        LocalDate today = date != null ? date : LocalDate.now();
        DashboardDto dto = new DashboardDto();

        long pending = teacherProfileRepository
                .findByVerificationStatus(TeacherProfile.VerificationStatus.PENDING).size();
        dto.setPendingTeacherVerifications(pending);

        List<ContractSession> activeContracts = contractRepository.findAll().stream()
                .filter(c -> c.getStatus() == ContractSession.ContractStatus.ACTIVE)
                .toList();
        dto.setTodaySessionsCount(activeContracts.size());

        int alertDays = appProperties.getSubscriptionExpiringAlertDays();
        LocalDate alertUntil = today.plusDays(alertDays);
        long subsExpiring = subscriptionRepository.findAll().stream()
                .filter(s -> s.getStatus() == Subscription.SubscriptionStatus.ACTIVE
                        && !s.getEndDate().isAfter(alertUntil) && !s.getEndDate().isBefore(today))
                .count();
        dto.setSubscriptionsExpiringSoon(subsExpiring);

        int contractAlertDays = appProperties.getContractExpiringAlertDays();
        long contractsExpiring = contractRepository.findAll().stream()
                .filter(c -> c.getStatus() == ContractSession.ContractStatus.ACTIVE)
                .filter(c -> {
                    LocalDate end = c.getSubscription() != null ? c.getSubscription().getEndDate() : c.getLegacyPeriodEnd();
                    return end != null && !end.isAfter(today.plusDays(contractAlertDays)) && !end.isBefore(today);
                })
                .count();
        dto.setContractsExpiringSoon(contractsExpiring);

        LocalDate monthStart = today.withDayOfMonth(1);
        List<AttendanceLog> monthLogs = attendanceLogRepository.findBySessionDateBetween(monthStart, today);
        double revenue = 0;
        for (AttendanceLog log : monthLogs) {
            if (log.getHoursUsed() != null && log.getContract() != null && log.getContract().getTeacher() != null) {
                Double rate = log.getContract().getTeacher().getHourlyRate();
                if (rate == null) rate = appProperties.getDefaultHourlyRateForRevenue();
                revenue += log.getHoursUsed() * rate;
            }
        }
        dto.setRevenueThisMonth(revenue);

        List<String> pendingActions = new ArrayList<>();
        if (pending > 0) pendingActions.add(pending + " teacher(s) pending verification");
        if (subsExpiring > 0) pendingActions.add(subsExpiring + " subscription(s) expiring soon");
        if (contractsExpiring > 0) pendingActions.add(contractsExpiring + " contract(s) expiring soon");
        dto.setPendingActions(pendingActions);
        dto.setAlerts(List.of());
        return dto;
    }
}
