package com.educonnect.application.admin.port;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Port for subscription commands. Implemented by persistence adapter.
 */
public interface SubscriptionCommandPort {

    Optional<LocalDate> renew(String subscriptionId, int additionalMonths);
}
