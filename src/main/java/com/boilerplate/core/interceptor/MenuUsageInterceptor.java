package com.boilerplate.core.interceptor;

import com.boilerplate.core.config.MenuClassifier;
import com.boilerplate.stats.service.StatisticsService;
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
public class MenuUsageInterceptor implements HandlerInterceptor {

    private final StatisticsService statisticsService;
    private final MenuClassifier menuClassifier;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String menuName = menuClassifier.classify(request.getRequestURI());
        String username = getUsername();

        // "ETC"나 "UNDEFINED" 같은 공통 메뉴는 통계에서 제외할 수 있음 (선택사항)
        if (!"ETC".equals(menuName) && !"UNDEFINED".equals(menuName)) {
            statisticsService.recordMenuUsage(menuName, username);
        }

        return true;
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            }
            return principal.toString();
        }
        return "anonymous";
    }
}
