package com.boilerplate.stats.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_unique_visitor",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"visit_date", "visitor_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyUniqueVisitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "visitor_id", nullable = false)
    private String visitorId;

    @Column(name = "visited_at", nullable = false, updatable = false)
    private LocalDateTime visitedAt;

    @PrePersist
    protected void onCreate() {
        if (visitDate == null) {
            visitDate = LocalDate.now();
        }
        if (visitedAt == null) {
            visitedAt = LocalDateTime.now();
        }
    }
}
