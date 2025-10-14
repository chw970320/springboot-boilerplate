package com.boilerplate.core.interceptor;

import com.boilerplate.stats.service.StatisticsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DailyVisitorInterceptor implements HandlerInterceptor {

    private static final String VISITOR_COOKIE_NAME = "_visitor_id";
    private static final int COOKIE_MAX_AGE = 365 * 24 * 60 * 60; // 1년

    private final StatisticsService statisticsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String visitorId = getVisitorId(request, response);
        statisticsService.recordDailyVisitor(visitorId);
        return true;
    }

    private String getVisitorId(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 1. 로그인 사용자 식별
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }

        // 2. 비로그인 사용자 (쿠키 기반 식별)
        return getOrSetVisitorCookie(request, response);
    }

    private String getOrSetVisitorCookie(HttpServletRequest request, HttpServletResponse response) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> VISITOR_COOKIE_NAME.equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElseGet(() -> createVisitorCookie(response)); // 쿠키는 있지만 _visitor_id가 없는 경우
        }
        // 쿠키가 아예 없는 경우
        return createVisitorCookie(response);
    }

    private String createVisitorCookie(HttpServletResponse response) {
        String newVisitorId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie(VISITOR_COOKIE_NAME, newVisitorId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(cookie);
        return newVisitorId;
    }
}
