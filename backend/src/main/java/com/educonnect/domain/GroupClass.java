package com.educonnect.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "group_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherProfile teacher;

    @Column(name = "zoom_join_url")
    private String zoomJoinUrl;  // set by teacher

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @ElementCollection
    @CollectionTable(name = "group_class_schedule_days", joinColumns = @JoinColumn(name = "group_class_id"))
    @Column(name = "day_of_week")
    private Set<Integer> daysOfWeek;

    @Column(name = "schedule_start_time")
    private LocalTime scheduleStartTime;

    @Column(name = "schedule_end_time")
    private LocalTime scheduleEndTime;

    /** Set when admin changes schedule (days/times); teacher can see when schedule was last updated. */
    @Column(name = "schedule_updated_at")
    private Instant scheduleUpdatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
