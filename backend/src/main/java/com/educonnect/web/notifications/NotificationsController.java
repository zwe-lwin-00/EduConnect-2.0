package com.educonnect.web.notifications;

import com.educonnect.domain.Notification;
import com.educonnect.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notifications for the current user (Admin, Teacher, Parent).
 * GET /notifications lists notifications; POST /notifications/mark-all-read marks all as read.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationsController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public List<NotificationDto> list() {
        String userId = currentUserId();
        if (userId == null) return List.of();
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/mark-all-read")
    @Transactional
    public ResponseEntity<Void> markAllRead() {
        String userId = currentUserId();
        if (userId != null) {
            notificationRepository.markAllReadByUserId(userId);
        }
        return ResponseEntity.noContent().build();
    }

    private static String currentUserId() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) return null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal != null ? principal.toString() : null;
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
