package com.educonnect.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings", uniqueConstraints = {
    @UniqueConstraint(columnNames = "key_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "key_name", nullable = false, unique = true)
    private String keyName;

    @Column(name = "value", length = 2000)
    private String value;

    @Column(length = 500)
    private String description;
}
