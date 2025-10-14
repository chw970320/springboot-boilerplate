package com.boilerplate.stats.domain;

import com.boilerplate.stats.dto.MenuUsageStatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface MenuUsageStatsRepositoryCustom {
    List<MenuUsageStatsResponse> findMenuUsageStatsByDateRange(LocalDate startDate, LocalDate endDate);
}
