package com.boilerplate.stats.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "menu_usage_stats",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"menu_name", "visit_date", "username"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuUsageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 1;
}
