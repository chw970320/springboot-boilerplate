package com.boilerplate.stats.domain;

import com.boilerplate.stats.dto.MenuUsageStatsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.boilerplate.stats.domain.QMenuUsageStats.menuUsageStats;

@RequiredArgsConstructor
public class MenuUsageStatsRepositoryImpl implements MenuUsageStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MenuUsageStatsResponse> findMenuUsageStatsByDateRange(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .select(Projections.constructor(MenuUsageStatsResponse.class,
                        menuUsageStats.menuName,
                        menuUsageStats.visitDate,
                        menuUsageStats.usageCount.sum().longValue()))
                .from(menuUsageStats)
                .where(menuUsageStats.visitDate.between(startDate, endDate))
                .groupBy(menuUsageStats.menuName, menuUsageStats.visitDate)
                .orderBy(menuUsageStats.visitDate.asc(), menuUsageStats.usageCount.sum().desc())
                .fetch();
    }
}
