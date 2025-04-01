package com.example.sims.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", referencedColumnName = "email",
            foreignKey = @ForeignKey(name = "fk_activity_log_user"))
    private User user;

    private String activityType;
    private String description;
    private String ipAddress;

    @CreationTimestamp
    private LocalDateTime createdAt;
    // Getters, setters, constructors
}
