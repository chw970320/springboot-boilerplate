package com.boilerplate.core.config;

import org.springframework.stereotype.Component;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 요청 URI를 기반으로 메뉴 이름을 분류하는 유틸리티 클래스.
 */
@Component
public class MenuClassifier {

    private static final Map<String, String> MENU_PATTERNS = new LinkedHashMap<>();

    static {
        // 인증
        MENU_PATTERNS.put("/api/auth/signup", "AUTH_SIGNUP");
        MENU_PATTERNS.put("/api/auth/login", "AUTH_LOGIN");
        MENU_PATTERNS.put("/api/auth/refresh", "AUTH_REFRESH");

        // 사용자 관리
        MENU_PATTERNS.put("/api/users/username", "USER_SEARCH");
        MENU_PATTERNS.put("/api/users", "USER_MANAGEMENT");

        // SSE
        MENU_PATTERNS.put("/events", "SSE_STREAMING");

        // API 문서
        MENU_PATTERNS.put("/swagger-ui", "API_DOCS");
        MENU_PATTERNS.put("/api-docs", "API_DOCS");

        // 모니터링
        MENU_PATTERNS.put("/actuator", "SYSTEM_MONITORING");
    }

    /**
     * URI를 기반으로 가장 적합한 메뉴 이름을 반환합니다.
     * @param uri 요청 URI
     * @return 분류된 메뉴 이름
     */
    public String classify(String uri) {
        if (uri == null) {
            return "UNDEFINED";
        }

        for (Map.Entry<String, String> entry : MENU_PATTERNS.entrySet()) {
            if (uri.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "ETC"; // 매칭되는 패턴이 없는 경우
    }
}
