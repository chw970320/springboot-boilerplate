package com.boilerplate.auth.service;

import com.boilerplate.auth.dto.AuthResponse;
import com.boilerplate.auth.dto.LoginRequest;
import com.boilerplate.auth.dto.SignupRequest;
import com.boilerplate.auth.security.JwtTokenProvider;
import com.boilerplate.core.exception.BusinessException;
import com.boilerplate.core.exception.ResourceAlreadyExistsException;
import com.boilerplate.user.domain.User;
import com.boilerplate.user.domain.UserRepository;
import com.boilerplate.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입을 처리합니다.
     */
    @Transactional
    public AuthResponse signup(SignupRequest signupRequest) {
        log.info("회원가입 시도 - username: {}", signupRequest.getUsername());

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already exists: " + signupRequest.getUsername());
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + signupRequest.getEmail());
        }

        User user = userMapper.signupRequestToUser(signupRequest);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 - ID: {}", savedUser.getId());

        AuthResponse authResponse = userMapper.toAuthResponse(savedUser);
        authResponse.setAccessToken(jwtTokenProvider.generateAccessToken(savedUser.getUsername()));
        authResponse.setRefreshToken(jwtTokenProvider.generateRefreshToken(savedUser.getUsername()));

        return authResponse;
    }

    /**
     * 로그인을 처리합니다.
     */
    public AuthResponse login(LoginRequest loginRequest) {
        log.info("로그인 시도 - username: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        AuthResponse authResponse = userMapper.toAuthResponse(user);
        authResponse.setAccessToken(jwtTokenProvider.generateAccessToken(user.getUsername()));
        authResponse.setRefreshToken(jwtTokenProvider.generateRefreshToken(user.getUsername()));

        log.info("로그인 성공 - ID: {}", user.getId());
        return authResponse;
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.
     */
    public AuthResponse refreshToken(String refreshToken) {
        log.info("토큰 갱신 시도");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        AuthResponse authResponse = userMapper.toAuthResponse(user);
        authResponse.setAccessToken(jwtTokenProvider.generateAccessToken(username));
        authResponse.setRefreshToken(refreshToken);

        log.info("토큰 갱신 완료 - username: {}", username);
        return authResponse;
    }
}
