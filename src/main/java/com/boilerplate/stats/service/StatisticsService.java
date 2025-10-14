package com.boilerplate.stats.service;

import com.boilerplate.stats.domain.DailyUniqueVisitor;
import com.boilerplate.stats.domain.DailyUniqueVisitorRepository;
import com.boilerplate.stats.domain.MenuUsageStatsRepository;
import com.boilerplate.stats.dto.DailyVisitorStatsResponse;
import com.boilerplate.stats.dto.MenuUsageStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 통계 관련 비즈니스 로직을 처리하는 서비스.
 * (DAU, 메뉴 사용 통계 등)
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticsService {

    private final DailyUniqueVisitorRepository dailyUniqueVisitorRepository;
    private final MenuUsageStatsRepository menuUsageStatsRepository;

    /**
     * 일일 순수 방문자(DAU)를 기록합니다.
     */
    @Async
    @Transactional
    public void recordDailyVisitor(String visitorId) {
        try {
            DailyUniqueVisitor visitor = DailyUniqueVisitor.builder()
                    .visitorId(visitorId)
                    .build();
            dailyUniqueVisitorRepository.save(visitor);
        } catch (DataIntegrityViolationException e) {
            log.debug("Visitor {} already recorded for today.", visitorId);
        } catch (Exception e) {
            log.error("Error recording daily visitor: {}", visitorId, e);
        }
    }

    /**
     * 메뉴 사용 통계를 기록합니다.
     */
    @Async
    @Transactional
    public void recordMenuUsage(String menuName, String username) {
        try {
            menuUsageStatsRepository.upsertUsageCount(menuName, LocalDate.now(), username);
        } catch (Exception e) {
            log.error("Error recording menu usage for menu: {}, user: {}", menuName, username, e);
        }
    }

    /**
     * 지정된 기간 동안의 일일 방문자 수(DAU) 통계를 조회합니다.
     */
    public List<DailyVisitorStatsResponse> getDailyVisitorStats(LocalDate startDate, LocalDate endDate) {
        return dailyUniqueVisitorRepository.findDailyVisitorStatsByDateRange(startDate, endDate);
    }

    /**
     * 지정된 기간 동안의 메뉴별 사용 통계를 조회합니다.
     */
    public List<MenuUsageStatsResponse> getMenuUsageStats(LocalDate startDate, LocalDate endDate) {
        return menuUsageStatsRepository.findMenuUsageStatsByDateRange(startDate, endDate);
    }
}
