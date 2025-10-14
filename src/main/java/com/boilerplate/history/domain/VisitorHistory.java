package com.boilerplate.history.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(length = 50)
    private String username;

    @Column(name = "visited_at", nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    @PrePersist
    protected void onCreate() {
        visitedAt = LocalDateTime.now();
    }
}
