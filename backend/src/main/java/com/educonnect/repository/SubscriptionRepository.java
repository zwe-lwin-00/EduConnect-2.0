package com.educonnect.repository;

import com.educonnect.domain.Subscription;
import com.educonnect.domain.Subscription.SubscriptionStatus;
import com.educonnect.domain.Subscription.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    List<Subscription> findByStudentId(String studentId);

    List<Subscription> findByStudentIdAndTypeAndStatus(String studentId, SubscriptionType type, SubscriptionStatus status);
}
