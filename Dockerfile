# ---------- 1) Build Stage ----------
# Java 21 JDK 이미지를 빌더로 사용
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

# Gradle 관련 파일을 먼저 복사하여 의존성 레이어 캐시 활용
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Gradle 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성을 별도 레이어로 분리하여 캐시 효율 극대화
RUN ./gradlew dependencies

# 소스 코드 복사
COPY src src

# 애플리케이션을 빌드하여 실행 가능한 JAR 생성 (QueryDSL Q-Type 생성 포함)
RUN ./gradlew bootJar

# ---------- 2) Runtime Stage ----------
# 더 가벼운 JRE 이미지를 사용하여 최종 이미지 크기 최적화
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# 빌더 스테이지에서 생성된 JAR 파일 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 파일 업로드 경로 생성 (애플리케이션 내에서 사용)
# Docker 볼륨 마운트를 통해 영속성을 확보하는 것을 권장합니다.
RUN mkdir -p uploads

# 애플리케이션 포트 노출
EXPOSE 8080

# 타임존 및 언어 설정
ENV TZ=Asia/Seoul
ENV LANG=C.UTF-8

# exec 형태로 ENTRYPOINT를 설정하여 Graceful Shutdown 보장
ENTRYPOINT ["java", "-jar", "app.jar"]
