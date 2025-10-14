package com.boilerplate.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * IP 화이트리스트 기반 접근 제어 인터셉터.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpWhitelistInterceptor implements HandlerInterceptor {

    @Value("${security.allowed-ips:}")
    private List<String> allowedIps;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 허용된 IP 목록이 비어있으면, 모든 IP를 허용
        if (CollectionUtils.isEmpty(allowedIps)) {
            return true;
        }

        String clientIp = getClientIp(request);

        if (allowedIps.contains(clientIp)) {
            log.debug("Allowed IP: {}", clientIp);
            return true;
        } else {
            log.warn("Forbidden IP: {}. Request to {} denied.", clientIp, request.getRequestURI());
            response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
            return false;
        }
    }

    /**
     * 프록시/로드밸런서 환경을 고려하여 클라이언트 IP를 조회합니다.
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }
}
