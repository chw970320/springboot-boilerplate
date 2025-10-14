package com.boilerplate.core.interceptor;

import com.boilerplate.history.service.VisitorHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class VisitorHistoryInterceptor implements HandlerInterceptor {

    private final VisitorHistoryService visitorHistoryService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String username = getUsername();

        // 비동기로 방문 이력 저장
        visitorHistoryService.saveVisitorHistory(requestUrl, method, ipAddress, userAgent, username);

        return true;
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
            return (String) principal;
        }
        return "anonymous";
    }
}
