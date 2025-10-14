package com.boilerplate;

import com.boilerplate.common.AbstractContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot 애플리케이션 컨텍스트 로드 테스트.
 */
@SpringBootTest
@ActiveProfiles("test")
class BoilerplateApplicationTests extends AbstractContainerTest { // AbstractContainerTest 상속

    @Test
    void contextLoads() {
        // 애플리케이션 컨텍스트가 성공적으로 로드되면 테스트 통과
    }
}
