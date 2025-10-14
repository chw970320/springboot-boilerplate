package com.boilerplate.history.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * VisitorHistory 엔티티에 대한 데이터 접근을 처리하는 리포지토리.
 */
@Repository
public interface VisitorHistoryRepository extends JpaRepository<VisitorHistory, Long> {
}
