package com.educonnect.application.shared;

import com.educonnect.domain.ContractSession;
import com.educonnect.domain.Subscription;

import java.time.LocalDate;

/**
 * Checks whether a contract has valid access for group class enrollment:
 * subscription-backed contracts require ACTIVE subscription and current date within period;
 * legacy contracts require legacyPeriodEnd >= today.
 */
public final class ContractEnrollmentAccess {

    private ContractEnrollmentAccess() {}

    /**
     * Returns true if the contract is ACTIVE and has access (subscription active and in period, or legacy end >= today).
     */
    public static boolean hasAccess(ContractSession contract) {
        if (contract == null || contract.getStatus() != ContractSession.ContractStatus.ACTIVE) {
            return false;
        }
        Subscription sub = contract.getSubscription();
        LocalDate today = LocalDate.now();
        if (sub != null) {
            return sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE
                    && !today.isBefore(sub.getStartDate())
                    && !today.isAfter(sub.getEndDate());
        }
        return contract.getLegacyPeriodEnd() != null && !today.isAfter(contract.getLegacyPeriodEnd());
    }
}
