# 스프링부트 보일러플레이트

REST API 기반 Spring Boot 보일러플레이트 프로젝트로 JWT 인증, PostgreSQL, MapStruct, WebFlux, QueryDSL 등의 기본 스펙을 가짐.
파일 업로드/다운로드, IP 화이트리스트, 방문 이력 및 통계 기능이 추가되었습니다.

## 🚀 Quick Start

### 1. 템플릿 받기
```bash
# GitHub Template 사용
"Use this template" 버튼 클릭

# 또는 Clone
git clone https://github.com/chw970320/springboot-boilerplate.git [프로젝트명]
cd [프로젝트명]
```

### 2. 프로젝트 설정 (최초 1회)
```powershell
# Windows
.\setup-project.ps1

# Linux/Mac
chmod +x setup-project.sh
./setup-project.sh
```

대화형으로 프로젝트명, 패키지명, DB명을 입력하면 자동으로 설정됩니다.

### 3. 데이터베이스 생성
```sql
CREATE DATABASE {your_db_name};
```

### 4. 실행

#### Local 환경 (기본)
```bash
./gradlew bootRun
# 또는 명시적으로
./gradlew bootRun --args='--spring.profiles.active=local'
```

#### Dev 환경
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

#### Prod 환경
```bash
java -jar build/libs/springboot-boilerplate-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Actuator**: http://localhost:8080/actuator

> 📖 **상세한 설정 가이드**: [SETUP_GUIDE.md](SETUP_GUIDE.md) 참고

## Docker를 이용한 배포

이 프로젝트는 멀티-스테이지 빌드를 지원하는 `Dockerfile`을 포함하고 있어, 빌드와 실행 환경을 분리하여 효율적이고 안전한 배포가 가능합니다.
파일 업로드 기능을 사용하는 경우, `uploads` 디렉토리를 위한 볼륨 마운트가 필요할 수 있습니다.

### 1. Docker 이미지 빌드

프로젝트 루트 디렉토리에서 아래 명령어를 실행하여 애플리케이션의 Docker 이미지를 빌드합니다. 이 이미지는 모든 환경(dev, prod 등)에서 재사용 가능한 범용 이미지입니다.

```bash
# "Build once, run anywhere"
# 태그는 원하는 대로 지정 가능 (예: myapp:latest, myapp:1.0.0)
docker build -t myapp:latest .
```

### 2. Docker 컨테이너 실행

빌드된 이미지를 사용하여 컨테이너를 실행합니다. 이때 `-e` 옵션을 통해 환경 변수를 주입하여 애플리케이션의 동작(프로필, DB 정보 등)을 제어합니다.
파일 업로드 기능을 사용하는 경우, `-v /path/on/host:/app/uploads`와 같이 볼륨을 마운트하여 영속성을 확보할 수 있습니다.

**Prod 환경 실행 예시:**
```bash
docker run -d --name myapp-prod -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://your-db-host:5432/your_prod_db \
  -e DB_USERNAME=your_prod_user \
  -e DB_PASSWORD=your_prod_password \
  -e JWT_SECRET=your-super-strong-and-long-jwt-secret-key \
  -v /path/to/your/uploads:/app/uploads \ # 파일 업로드 경로
  myapp:latest
```

**Dev 환경 실행 예시:**
```bash
docker run -d --name myapp-dev -p 8081:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e DB_URL=jdbc:postgresql://your-db-host:5432/your_dev_db \
  -e DB_USERNAME=your_dev_user \
  -e DB_PASSWORD=your_dev_password \
  -e JWT_SECRET=your-dev-jwt-secret-key \
  -v /path/to/your/uploads:/app/uploads \ # 파일 업로드 경로
  myapp:latest
```

## 환경별 설정 (Profiles)

프로젝트는 3가지 프로필을 지원합니다:

| Profile | 용도 | 기본 설정 | Swagger |
|---------|------|-----------|---------|
| **local** | 로컬 개발 | ddl-auto: update, 상세 로그 | ✅ 활성화 |
| **dev** | 개발 서버 | ddl-auto: validate, 중간 로그 | ✅ 활성화 |
| **prod** | 프로덕션 | ddl-auto: validate, 최소 로그 | ❌ 비활성화 |

### 프로필 전환 방법

```bash
# 1. 커맨드 라인
./gradlew bootRun --args='--spring.profiles.active=dev'

# 2. 환경 변수
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun

# 3. JAR 실행
java -jar app.jar --spring.profiles.active=prod

# 4. IDE (IntelliJ)
Run Configuration → Environment Variables → SPRING_PROFILES_ACTIVE=local
```

## 주요 기능

### 1. 파일 업로드/다운로드
- `application.yml`의 `file.upload-dir`에 지정된 경로에 파일을 저장합니다.
- `file.allowed-extensions`에 설정된 확장자만 업로드를 허용합니다.
- 업로드된 파일의 메타데이터를 데이터베이스에 저장합니다.

### 2. IP 화이트리스트 기반 접근 제어
- `application.yml`의 `security.allowed-ips`에 설정된 IP 주소에서만 API 접근을 허용합니다.
- 목록이 비어있으면 모든 IP를 허용합니다.

### 3. 방문 이력 및 통계
- **상세 방문 이력**: 모든 HTTP 요청에 대한 상세 정보를 기록합니다.
- **일일 순수 방문자 (DAU)**: 쿠키 기반으로 일일 순수 방문자 수를 집계합니다.
- **메뉴별 사용 통계**: API 엔드포인트를 메뉴로 분류하여 사용 횟수를 집계합니다.

## 기술 스택

### 코어 프레임워크
- **Java 21**
- **Spring Boot 3.2.0**
- **Spring MVC** - REST API
- **Spring WebFlux** - SSE/스트리밍 (비동기)

### 데이터 & 영속성
- **Spring Data JPA** - ORM 데이터 접근
- **QueryDSL** - 타입-세이프한 쿼리 작성
- **PostgreSQL** - 관계형 데이터베이스
- **Flyway** - DB 마이그레이션
- **EhCache** - 2차 캐시 (JCache/JSR-107)
- **HikariCP** - 커넥션 풀

### 보안 & 인증
- **Spring Security** - 보안 프레임워크
- **JWT (JJWT)** - 토큰 기반 인증
- **OAuth2 Resource Server** - 확장 가능
- **BCrypt** - 비밀번호 암호화

### 도구 & 유틸리티
- **MapStruct** - DTO-Entity 매핑 자동화
- **Lombok** - 코드 간소화
- **SpringDoc OpenAPI** - API 문서 자동화 (Swagger UI)
- **Spring Boot Actuator** - 모니터링 & 헬스체크
- **AspectJ** - AOP 로깅

### 빌드 & 테스트
- **Gradle (Kotlin DSL)** - 빌드 도구
- **JUnit 5** - 테스트 프레임워크
- **Testcontainers** - 통합 테스트 (Docker 기반)

## 프로젝트 구조

```
src/main/java/com/boilerplate/
├── BoilerplateApplication.java          # 메인 애플리케이션 클래스
├── auth/                                 # 인증 및 권한 관련 도메인
│   ├── api/                              # 인증 컨트롤러
│   ├── dto/                              # 인증 관련 DTO
│   ├── security/                         # JWT 토큰, 필터 등 보안 관련
│   └── service/                          # 인증 서비스, UserDetailsService
├── core/                                 # 공통 및 핵심 기능
│   ├── aop/                              # AOP 로깅
│   ├── common/                           # 공통 응답 (ApiResponse)
│   ├── config/                           # 공통 설정 (WebConfig, QuerydslConfig, MenuClassifier)
│   ├── exception/                        # 공통 예외 처리
│   └── interceptor/                      # 공통 인터셉터 (IP Whitelist, Logging, Visitor History, Daily Visitor, Menu Usage)
├── event/                                # SSE 스트리밍 관련 도메인
│   ├── api/                              # SSE 이벤트 핸들러
│   └── config/                           # WebFlux 라우터 설정
├── file/                                 # 파일 업로드/다운로드 관련 도메인
│   ├── api/                              # 파일 컨트롤러
│   ├── domain/                           # 파일 첨부 엔티티, 리포지토리
│   ├── dto/                              # 파일 관련 DTO
│   ├── exception/                        # 파일 관련 예외
│   └── service/                          # 파일 저장 서비스
├── history/                              # 방문 상세 이력 관련 도메인
│   ├── api/                              # 방문 이력 컨트롤러
│   ├── domain/                           # 방문 이력 엔티티, 리포지토리
│   ├── dto/                              # 방문 이력 DTO
│   ├── mapper/                           # 방문 이력 매퍼
│   └── service/                          # 방문 이력 서비스
├── stats/                                # 통계 관련 도메인
│   ├── api/                              # 통계 컨트롤러
│   ├── domain/                           # 통계 엔티티, 리포지토리
│   ├── dto/                              # 통계 DTO
│   └── service/                          # 통계 서비스
└── user/                                 # 사용자 관리 관련 도메인
    ├── api/                              # 사용자 컨트롤러
    ├── domain/                           # 사용자 엔티티, 리포지토리, Role Enum
    ├── dto/                              # 사용자 관련 DTO
    ├── mapper/                           # 사용자 매퍼
    └── service/                          # 사용자 서비스

src/main/resources/
├── application.yml                       # 공통 설정 (파일 경로, IP 화이트리스트, 허용 확장자 포함)
├── application-local.yml                 # 로컬 환경 설정 (캐시 활성화)
├── application-dev.yml                   # 개발 서버 설정 (캐시 활성화)
├── application-prod.yml                  # 프로덕션 설정 (캐시 활성화)
├── ehcache.xml                          # EhCache 2차 캐시 설정
└── db/migration/
    └── V1__init.sql                     # Flyway 초기 스키마 (모든 테이블 정의 포함)

src/test/java/com/boilerplate/
├── BoilerplateApplicationTests.java     # 통합 테스트
└── user/
    └── domain/
        └── UserRepositoryTest.java          # Repository 슬라이스 테스트
```

## 시작하기

### 필수 조건

- **Java 21** 이상
- **PostgreSQL 12** 이상
- **Docker** (Testcontainers 테스트용, 선택사항)
- **Gradle 8.5** 이상 (wrapper 포함)

### 빠른 시작

1. **저장소 클론**
```bash
git clone https://github.com/your-repo/springboot-boilerplate.git
cd springboot-boilerplate
```

2. **PostgreSQL 데이터베이스 생성**
```sql
CREATE DATABASE service_db;
```

3. **데이터베이스 연결 설정** (`src/main/resources/application.yml` 또는 프로필 파일)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/service_db
    username: {username}
    password: {password}
```

4. **JWT Secret Key 변경**
```yaml
jwt:
  secret: {secure-secret-key}
```

5. **파일 업로드 경로 및 허용 확장자 설정** (`src/main/resources/application.yml` 또는 프로필 파일)
```yaml
file:
  upload-dir: ./uploads # 파일이 저장될 경로 (Docker 사용 시 볼륨 마운트 고려)
  allowed-extensions: ["jpg", "jpeg", "png", "gif", "pdf", "txt", "zip"] # 허용할 확장자 목록
```

6. **IP 화이트리스트 설정 (선택 사항)** (`src/main/resources/application.yml` 또는 프로필 파일)
```yaml
security:
  allowed-ips: ["127.0.0.1", "192.168.1.100"] # 허용할 IP 목록. 비어있으면 모든 IP 허용.
```

7. **Gradle Wrapper 권한 부여** (Linux/Mac)
```bash
chmod +x gradlew
```

8. **애플리케이션 빌드 및 실행**
```bash
# 빌드 (QueryDSL Q-Type 생성 포함)
./gradlew clean build

# 실행
./gradlew bootRun
```

또는 JAR 파일로 실행:
```bash
java -jar build/libs/springboot-boilerplate-0.0.1-SNAPSHOT.jar
```

### 접속 URL

- **REST API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8080/api-docs
- **Actuator**: http://localhost:8080/actuator
- **SSE 스트리밍**: http://localhost:8080/events

## API 엔드포인트

모든 API 엔드포인트는 **Swagger UI**에서 테스트할 수 있습니다: http://localhost:8080/swagger-ui.html

### 인증 (`/api/auth/**`)
- `POST /api/auth/signup`: 회원가입
- `POST /api/auth/login`: 로그인 (JWT 발급)
- `POST /api/auth/refresh`: 토큰 갱신

### 사용자 관리 (`/api/users/**`)
- `GET /api/users`: 모든 사용자 조회 (ADMIN)
- `GET /api/users/{id}`: ID로 사용자 조회 (본인 또는 ADMIN)
- `GET /api/users/username/{username}`: 사용자명으로 조회 (본인 또는 ADMIN)
- `DELETE /api/users/{id}`: 사용자 삭제 (ADMIN)

### 파일 관리 (`/api/files/**`)
- `POST /api/files/upload`: 파일 업로드 (인증 필요)
- `GET /api/files/download/{fileName}`: 파일 다운로드 (인증 필요)

### 방문 이력 (`/api/history/**`)
- `GET /api/history`: 방문 상세 이력 조회 (ADMIN, 페이지네이션)

### 통계 (`/api/stats/**`)
- `GET /api/stats/dau`: 일일 순수 방문자(DAU) 통계 조회 (ADMIN, 기간별)
- `GET /api/stats/menu`: 메뉴별 사용 통계 조회 (ADMIN, 기간별)

### WebFlux 스트리밍 (`/events/**`)
- `GET /events`: 전역 이벤트 스트림 (SSE)
- `GET /events/user/{user-id}`: 사용자별 이벤트 스트림 (SSE)

### Actuator 엔드포인트 (`/actuator/**`)
- `GET /actuator/health`: 헬스체크
- `GET /actuator/info`: 애플리케이션 정보
- `GET /actuator/metrics`: 메트릭
- `GET /actuator/prometheus`: Prometheus 메트릭

## 보안 설정

### JWT 토큰
- Access Token 유효 기간: 24시간
- Refresh Token 유효 기간: 7일
- HMAC-SHA256 알고리즘 사용

### 권한 관리
- **Public 엔드포인트:** `/api/auth/**`, `/api/public/**`, `/swagger-ui/**`, `/api-docs/**`, `/actuator/**`, `/events/**`
- **인증 필요:** 모든 `/api/**` 엔드포인트
- **ADMIN만 접근:** `/api/users` (GET all), `/api/users/{id}` (DELETE), `/api/history`, `/api/stats/**`

### IP 화이트리스트
- `application.yml`의 `security.allowed-ips` 설정으로 접근 IP 제한 가능.

## 환경별 설정

프로덕션 환경에서는 다음 사항을 변경해야 합니다:

1. JWT Secret Key를 강력한 키로 변경
2. 데이터베이스 연결 정보 변경
3. `spring.jpa.hibernate.ddl-auto`를 `validate` 또는 `none`으로 변경
4. 로깅 레벨 조정
5. CORS 허용 오리진 제한
6. `file.upload-dir` 경로를 영속적인 스토리지로 변경 (Docker 사용 시 볼륨 마운트)
7. `security.allowed-ips`를 실제 운영 환경 IP로 설정

## 테스트

### 전체 테스트 실행
```bash
./gradlew test
```

### Testcontainers 통합 테스트
프로젝트는 Testcontainers를 사용하여 실제 PostgreSQL 환경에서 테스트합니다.
Docker가 실행 중이어야 합니다.

```bash
# 통합 테스트
./gradlew test --tests BoilerplateApplicationTests

# Repository 슬라이스 테스트
./gradlew test --tests UserRepositoryTest
```

### 테스트 커버리지
```bash
./gradlew jacocoTestReport
```

## 빌드

### 개발용 빌드
```bash
./gradlew build
```

### 프로덕션 빌드
```bash
./gradlew clean build -x test
```

빌드된 JAR 파일: `build/libs/springboot-boilerplate-0.0.1-SNAPSHOT.jar`

### Flyway 마이그레이션

새 마이그레이션 추가:

1. `src/main/resources/db/migration/` 디렉토리에 파일 생성
2. 파일명 규칙: `V{버전}__{설명}.sql` (예: `V2__add_user_profile.sql`)
3. 애플리케이션 재시작 시 자동 적용

```sql
-- V2__add_user_profile.sql
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
ALTER TABLE users ADD COLUMN address TEXT;
```

### WebFlux SSE 커스터마이징

새로운 스트리밍 엔드포인트 추가:

```java
// in: com.boilerplate.event.api.EventHandler.java
@Component
public class EventHandler {
    public Mono<ServerResponse> customStream(ServerRequest req) {
        Flux<String> stream = // 커스텀 Flux 생성
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stream, String.class);
    }
}

// in: com.boilerplate.event.config.WebFluxConfig.java
@Configuration
public class WebFluxConfig {
    @Bean
    public RouterFunction<ServerResponse> routes(EventHandler handler) {
        return route(GET("/custom-events"), handler::customStream);
    }
}
