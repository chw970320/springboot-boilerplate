package com.boilerplate.stats.domain;

import com.boilerplate.stats.dto.DailyVisitorStatsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.boilerplate.stats.domain.QDailyUniqueVisitor.dailyUniqueVisitor;

@RequiredArgsConstructor
public class DailyUniqueVisitorRepositoryImpl implements DailyUniqueVisitorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<DailyVisitorStatsResponse> findDailyVisitorStatsByDateRange(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .select(Projections.constructor(DailyVisitorStatsResponse.class,
                        dailyUniqueVisitor.visitDate,
                        dailyUniqueVisitor.visitorId.countDistinct()))
                .from(dailyUniqueVisitor)
                .where(dailyUniqueVisitor.visitDate.between(startDate, endDate))
                .groupBy(dailyUniqueVisitor.visitDate)
                .orderBy(dailyUniqueVisitor.visitDate.asc())
                .fetch();
    }
}
