package com.educonnect.web.admin;

import com.educonnect.application.admin.usecase.RenewSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Admin feature: subscription renew endpoint.
 */
@RestController
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
public class AdminSubscriptionController {

    private final RenewSubscriptionUseCase renewSubscriptionUseCase;

    @PostMapping("/{id}/renew")
    public ResponseEntity<?> renew(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int additionalMonths) {
        Optional<java.time.LocalDate> newEnd = renewSubscriptionUseCase.execute(id, additionalMonths);
        return newEnd
                .map(endDate -> ResponseEntity.ok(Map.of(
                        "id", id,
                        "endDate", endDate.toString(),
                        "message", "Subscription extended by " + additionalMonths + " month(s)"
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
