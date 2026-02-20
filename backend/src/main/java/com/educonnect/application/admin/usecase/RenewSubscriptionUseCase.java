package com.educonnect.application.admin.usecase;

import com.educonnect.application.admin.port.SubscriptionCommandPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class RenewSubscriptionUseCase {

    private final SubscriptionCommandPort subscriptionCommandPort;

    public RenewSubscriptionUseCase(SubscriptionCommandPort subscriptionCommandPort) {
        this.subscriptionCommandPort = subscriptionCommandPort;
    }

    public Optional<LocalDate> execute(String subscriptionId, int additionalMonths) {
        return subscriptionCommandPort.renew(subscriptionId, additionalMonths);
    }
}
