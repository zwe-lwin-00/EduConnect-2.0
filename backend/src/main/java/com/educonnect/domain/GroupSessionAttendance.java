package com.educonnect.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_session_attendances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSessionAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_session_id", nullable = false)
    private GroupSession groupSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "hours_used")
    private Double hoursUsed;
}
