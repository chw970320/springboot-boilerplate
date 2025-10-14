package com.boilerplate.stats.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DailyUniqueVisitor 엔티티에 대한 데이터 접근을 처리하는 리포지토리.
 */
@Repository
public interface DailyUniqueVisitorRepository extends JpaRepository<DailyUniqueVisitor, Long>, DailyUniqueVisitorRepositoryCustom {
}
