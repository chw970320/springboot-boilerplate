package com.boilerplate.stats.domain;

import com.boilerplate.common.AbstractContainerTest;
import com.boilerplate.config.TestJpaConfig;
import com.boilerplate.stats.dto.DailyVisitorStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class DailyUniqueVisitorRepositoryTest extends AbstractContainerTest { // AbstractContainerTest 상속

    @Autowired
    private DailyUniqueVisitorRepository dailyUniqueVisitorRepository;

    @BeforeEach
    void setUp() {
        dailyUniqueVisitorRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("일일 순수 방문자 저장 테스트")
    void testSaveDailyUniqueVisitor() {
        // Given
        DailyUniqueVisitor visitor = DailyUniqueVisitor.builder()
                .visitorId("user1")
                .visitDate(LocalDate.of(2025, 10, 14))
                .visitedAt(LocalDateTime.now())
                .build();

        // When
        DailyUniqueVisitor saved = dailyUniqueVisitorRepository.save(visitor);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVisitorId()).isEqualTo("user1");
        assertThat(saved.getVisitDate()).isEqualTo(LocalDate.of(2025, 10, 14));
    }

    @Test
    @DisplayName("동일 날짜 동일 방문자 중복 저장 방지 테스트")
    void testSaveDailyUniqueVisitorDuplicate() {
        // Given
        LocalDate date = LocalDate.of(2025, 10, 14);
        DailyUniqueVisitor visitor1 = DailyUniqueVisitor.builder()
                .visitorId("user1")
                .visitDate(date)
                .visitedAt(LocalDateTime.now())
                .build();
        dailyUniqueVisitorRepository.saveAndFlush(visitor1);

        DailyUniqueVisitor visitor2 = DailyUniqueVisitor.builder()
                .visitorId("user1")
                .visitDate(date)
                .visitedAt(LocalDateTime.now().plusHours(1))
                .build();

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            dailyUniqueVisitorRepository.saveAndFlush(visitor2);
        });
    }

    @Test
    @DisplayName("QueryDSL을 이용한 일일 방문자 통계 조회 테스트")
    void testFindDailyVisitorStatsByDateRange() {
        // Given
        LocalDate today = LocalDate.of(2025, 10, 14);
        LocalDate yesterday = today.minusDays(1);

        dailyUniqueVisitorRepository.save(DailyUniqueVisitor.builder().visitorId("user1").visitDate(yesterday).visitedAt(LocalDateTime.now()).build());
        dailyUniqueVisitorRepository.save(DailyUniqueVisitor.builder().visitorId("user2").visitDate(yesterday).visitedAt(LocalDateTime.now()).build());
        dailyUniqueVisitorRepository.save(DailyUniqueVisitor.builder().visitorId("user3").visitDate(today).visitedAt(LocalDateTime.now()).build());

        // When
        List<DailyVisitorStatsResponse> stats = dailyUniqueVisitorRepository.findDailyVisitorStatsByDateRange(yesterday, today);

        // Then
        assertThat(stats).hasSize(2);
        assertThat(stats)
                .extracting("visitDate", "visitorCount")
                .containsExactlyInAnyOrder(
                        tuple(yesterday, 2L),
                        tuple(today, 1L)
                );
    }
}
