package com.boilerplate.user.service;

import com.boilerplate.core.exception.ResourceNotFoundException;
import com.boilerplate.user.domain.User;
import com.boilerplate.user.domain.UserRepository;
import com.boilerplate.user.dto.UserResponse;
import com.boilerplate.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 서비스.
 */
@Slf4j
@Service("userService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 모든 사용자를 조회합니다.
     */
    public List<UserResponse> getAllUsers() {
        log.info("전체 사용자 조회");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    /**
     * ID로 사용자를 조회합니다.
     *
     * <p>조회된 사용자는 캐시에 저장됩니다.</p>
     */
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        log.info("사용자 조회 by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    /**
     * 사용자명으로 사용자를 조회합니다.
     */
    public UserResponse getUserByUsername(String username) {
        log.info("사용자 조회 by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return userMapper.toDto(user);
    }

    /**
     * 사용자를 삭제합니다.
     *
     * <p>삭제 시 캐시에서도 제거됩니다.</p>
     */
    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        log.info("사용자 삭제 - ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    /**
     * 인증된 사용자가 조회하려는 리소스의 소유자인지 확인합니다.
     *
     * @param username 인증된 사용자의 이름
     * @param id       확인할 사용자의 ID
     * @return 소유자이면 true, 아니면 false
     */
    public boolean isOwner(String username, Long id) {
        return userRepository.findById(id)
                .map(user -> user.getUsername().equals(username))
                .orElse(false);
    }
}
