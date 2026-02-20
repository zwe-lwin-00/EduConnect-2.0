package com.educonnect.infrastructure.persistence.adapter;

import com.educonnect.application.admin.port.SubscriptionCommandPort;
import com.educonnect.domain.Subscription;
import com.educonnect.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class SubscriptionPersistenceAdapter implements SubscriptionCommandPort {

    private final SubscriptionRepository repository;

    public SubscriptionPersistenceAdapter(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<LocalDate> renew(String subscriptionId, int additionalMonths) {
        return repository.findById(subscriptionId)
                .map(sub -> {
                    LocalDate newEnd = sub.getEndDate().plusMonths(additionalMonths);
                    sub.setEndDate(newEnd);
                    repository.save(sub);
                    return newEnd;
                });
    }
}
