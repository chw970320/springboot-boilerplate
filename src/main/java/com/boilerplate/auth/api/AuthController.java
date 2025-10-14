package com.boilerplate.auth.api;

import com.boilerplate.core.common.ApiResponse;
import com.boilerplate.auth.dto.AuthResponse;
import com.boilerplate.auth.dto.LoginRequest;
import com.boilerplate.auth.dto.SignupRequest;
import com.boilerplate.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입.
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("회원가입 요청 - username: {}", signupRequest.getUsername());
        AuthResponse authResponse = authService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Signup successful", authResponse));
    }

    /**
     * 로그인.
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("로그인 요청 - username: {}", loginRequest.getUsername());
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * 토큰 갱신.
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급합니다")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestParam String refreshToken) {
        log.info("토큰 갱신 요청");
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));
    }
}
