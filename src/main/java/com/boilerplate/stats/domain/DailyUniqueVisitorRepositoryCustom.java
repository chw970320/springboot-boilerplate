package com.boilerplate.stats.domain;

import com.boilerplate.stats.dto.DailyVisitorStatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface DailyUniqueVisitorRepositoryCustom {
    List<DailyVisitorStatsResponse> findDailyVisitorStatsByDateRange(LocalDate startDate, LocalDate endDate);
}
