plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.flywaydb.flyway") version "11.14.0" // Flyway Gradle 플러그인
}

group = "com.boilerplate"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Flyway 전용 의존성 구성
configurations {
    create("flyway")
}

// QueryDSL 설정 추가
val queryDslVersion = "5.0.0"

// 생성될 Q-Type 클래스들의 위치를 지정
sourceSets {
    main {
        java {
            srcDirs("src/main/java", "build/generated/querydsl")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // QueryDSL 의존성 추가
    implementation("com.querydsl:querydsl-jpa:${queryDslVersion}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Spring WebFlux (SSE/스트리밍 전용)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Spring Boot Actuator (모니터링)
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // PostgreSQL (애플리케이션 런타임용)
    runtimeOnly("org.postgresql:postgresql:42.6.0")

    // Flyway 플러그인 전용 의존성
    "flyway"("org.flywaydb:flyway-database-postgresql:11.14.0")
    "flyway"("org.postgresql:postgresql:42.6.0")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // MapStruct (DTO-Entity 매퍼)
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // springdoc-openapi (Swagger UI)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // Cache (EhCache for JPA 2nd level cache)
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.hibernate:hibernate-jcache:6.3.1.Final")
    implementation("org.ehcache:ehcache:3.10.8")
    implementation("javax.cache:cache-api:1.1.1")

    // JAXB (Java 11+ 필수 - EhCache XML 파싱에 필요)
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.9")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.postgresql:postgresql:42.6.0")

    // Testcontainers (DB 테스트 격리)
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}

// QueryDSL Q-Type 생성 clean 태스크
tasks.register("cleanQuerydsl", Delete::class) {
    delete(file("build/generated/querydsl"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed") // 성공, 스킵, 실패한 테스트를 모두 표시
        showStandardStreams = true // 표준 출력 (System.out, System.err) 표시
    }
    systemProperty("file.encoding", "UTF-8")
}
