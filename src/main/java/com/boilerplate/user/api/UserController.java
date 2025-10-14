package com.boilerplate.user.api;

import com.boilerplate.core.common.ApiResponse;
import com.boilerplate.user.dto.UserResponse;
import com.boilerplate.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관리 REST API 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    /**
     * 전체 사용자 목록 조회 (ADMIN만 가능).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 목록을 조회합니다 (ADMIN 권한 필요)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("전체 사용자 목록 조회 요청");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * ID로 사용자 조회 (본인 또는 ADMIN만 가능).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isOwner(authentication.name, #id)")
    @Operation(summary = "사용자 조회 (ID)", description = "ID로 사용자 정보를 조회합니다 (본인 또는 ADMIN만 가능)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("사용자 조회 요청 - ID: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 사용자명으로 사용자 검색 (본인 또는 ADMIN만 가능).
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
    @Operation(summary = "사용자 검색 (Username)", description = "사용자명으로 사용자 정보를 검색합니다 (본인 또는 ADMIN만 가능)")
    public ResponseEntity<ApiResponse<UserResponse>> searchUserByUsername(@RequestParam String username) {
        log.info("사용자 검색 요청 - username: {}", username);
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * 사용자 삭제 (ADMIN만 가능).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다 (ADMIN 권한 필요)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("사용자 삭제 요청 - ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
