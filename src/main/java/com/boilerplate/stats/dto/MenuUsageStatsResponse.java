package com.boilerplate.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuUsageStatsResponse {
    private String menuName;
    private LocalDate visitDate;
    private Long usageCount;
}
