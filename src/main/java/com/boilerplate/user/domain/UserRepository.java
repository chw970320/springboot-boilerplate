package com.boilerplate.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티에 대한 Spring Data JPA Repository.
 * 
 * <p>Spring Data JPA가 제공하는 기본 CRUD 메서드 외에
 * 사용자명과 이메일 기반의 조회/존재 여부 확인 메서드를 제공합니다.</p>
 * 
 * @author chw970320
 * @version 1.0
 * @since 2025-10-10
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자를 조회합니다.
     * 
     * @param username 조회할 사용자명
     * @return Optional<User> 사용자 (존재하지 않으면 empty)
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자를 조회합니다.
     * 
     * @param email 조회할 이메일
     * @return Optional<User> 사용자 (존재하지 않으면 empty)
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부를 확인합니다.
     * 
     * @param username 확인할 사용자명
     * @return Boolean 존재 여부
     */
    Boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부를 확인합니다.
     * 
     * @param email 확인할 이메일
     * @return Boolean 존재 여부
     */
    Boolean existsByEmail(String email);
}
