package com.boilerplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot 애플리케이션 메인 클래스.
 *
 * <p>이 클래스는 Spring Boot 애플리케이션의 진입점(Entry Point)입니다.
 * {@link SpringBootApplication} 애너테이션을 통해 다음 기능들이 자동으로 활성화됩니다:</p>
 *
 * <ul>
 *   <li>@Configuration: Spring 설정 클래스로 등록</li>
 *   <li>@EnableAutoConfiguration: Spring Boot의 자동 설정 활성화</li>
 *   <li>@ComponentScan: 컴포넌트 스캔 활성화 (현재 패키지 및 하위 패키지)</li>
 * </ul>
 *
 * <p>{@link EnableCaching}은 Spring의 캐싱 기능을 활성화합니다.</p>
 * <p>{@link EnableAsync}은 Spring의 비동기 메서드 실행 기능을 활성화합니다.</p>
 *
 * @author chw970320
 * @version 1.0
 * @since 2025-10-10
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableJpaAuditing
public class BoilerplateApplication {

    /**
     * 애플리케이션 메인 메서드.
     *
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(BoilerplateApplication.class, args);
    }
}
