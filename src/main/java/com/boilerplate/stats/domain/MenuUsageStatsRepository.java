package com.boilerplate.stats.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * MenuUsageStats 엔티티에 대한 데이터 접근을 처리하는 리포지토리.
 */
@Repository
public interface MenuUsageStatsRepository extends JpaRepository<MenuUsageStats, Long>, MenuUsageStatsRepositoryCustom {

    /**
     * 메뉴 사용 통계를 UPSERT(Update or Insert)합니다.
     * PostgreSQL의 ON CONFLICT 기능을 사용한 네이티브 쿼리입니다.
     */
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO menu_usage_stats (menu_name, visit_date, username, usage_count) " +
                   "VALUES (:menuName, :visitDate, :username, 1) " +
                   "ON CONFLICT (menu_name, visit_date, username) DO UPDATE " +
                   "SET usage_count = menu_usage_stats.usage_count + 1",
           nativeQuery = true)
    void upsertUsageCount(@Param("menuName") String menuName,
                          @Param("visitDate") LocalDate visitDate,
                          @Param("username") String username);

    /**
     * 메뉴명, 방문일자, 사용자명으로 통계 데이터를 조회합니다.
     */
    Optional<MenuUsageStats> findByMenuNameAndVisitDateAndUsername(String menuName, LocalDate visitDate, String username);
}
