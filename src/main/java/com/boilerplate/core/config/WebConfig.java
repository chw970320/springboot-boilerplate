package com.boilerplate.core.config;

import com.boilerplate.core.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final IpWhitelistInterceptor ipWhitelistInterceptor;
    private final LoggingInterceptor loggingInterceptor;
    private final VisitorHistoryInterceptor visitorHistoryInterceptor;
    private final DailyVisitorInterceptor dailyVisitorInterceptor;
    private final MenuUsageInterceptor menuUsageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 0. IP 화이트리스트 인터셉터 (가장 먼저 실행)
        registry.addInterceptor(ipWhitelistInterceptor)
                .addPathPatterns("/**")
                .order(0);

        // 1. 순수 로깅 인터셉터
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1);

        // 2. 방문 상세 이력(감사 로그) 인터셉터
        registry.addInterceptor(visitorHistoryInterceptor)
                .addPathPatterns("/**")
                .order(2);

        // 3. 일일 순수 방문자(DAU) 집계 인터셉터
        registry.addInterceptor(dailyVisitorInterceptor)
                .addPathPatterns("/**")
                .order(3);

        // 4. 메뉴별 사용 통계 집계 인터셉터
        registry.addInterceptor(menuUsageInterceptor)
                .addPathPatterns("/**")
                .order(4);
    }
}
