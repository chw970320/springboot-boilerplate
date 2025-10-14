package com.boilerplate.stats.domain;

import com.boilerplate.common.AbstractContainerTest;
import com.boilerplate.config.TestJpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class MenuUsageStatsRepositoryTest extends AbstractContainerTest { // AbstractContainerTest 상속

    @Autowired
    private MenuUsageStatsRepository menuUsageStatsRepository;

    @BeforeEach
    void setUp() {
        menuUsageStatsRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("메뉴 사용 통계 저장 테스트")
    void testSaveMenuUsageStats() {
        // Given
        MenuUsageStats stats = MenuUsageStats.builder()
                .menuName("menu1") // menuId 대신 menuName 사용
                .visitDate(LocalDate.of(2025, 10, 14)) // visitDate 추가
                .username("user1") // username 추가
                .usageCount(10) // Integer 타입으로 수정
                .build();

        // When
        MenuUsageStats saved = menuUsageStatsRepository.save(stats);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMenuName()).isEqualTo("menu1"); // getMenuId 대신 getMenuName 사용
        assertThat(saved.getUsageCount()).isEqualTo(10);
    }

}
