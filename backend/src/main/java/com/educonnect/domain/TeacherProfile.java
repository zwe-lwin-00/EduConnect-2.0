package com.educonnect.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private ApplicationUser user;

    @Column(name = "nrc_encrypted")
    private String nrcEncrypted;

    private String education;
    @Column(length = 2000)
    private String bio;

    @ElementCollection
    @CollectionTable(name = "teacher_specializations", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "specialization")
    @Builder.Default
    private java.util.Set<String> specializations = new java.util.HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private Double hourlyRate;  // optional; used for revenue reporting only

    @Column(name = "zoom_join_url")
    private String zoomJoinUrl;  // default for 1:1 sessions

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }
}
