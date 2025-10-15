# 프로젝트 설정 가이드

Spring Boot Boilerplate를 사용하여 새 프로젝트를 시작하는 방법입니다. 이 가이드는 프로젝트의 최신 구조와 기능을 반영합니다.

## 🚀 빠른 시작

### 1. 템플릿 받기

#### 프로젝트 클론
```bash
git clone https://github.com/chw970320/springboot-boilerplate.git [프로젝트명]
cd [프로젝트명]
```

### 2. 프로젝트 설정 (최초 1회)

#### Windows (PowerShell)
```powershell
.\setup-project.ps1
```

#### Windows (Batch)
```batch
setup-project.bat
```
또는 파일을 더블클릭

#### Linux / Mac
```bash
chmod +x setup-project.sh
./setup-project.sh
```

### 3. 대화형 설정

스크립트를 실행하면 다음 정보를 입력받습니다:

```
프로젝트명: myService
패키지명: (엔터: com.myservice) 
DB 이름: (엔터: myservice_db)

변경사항:
  • com.boilerplate → com.myservice
  • BoilerplateApplication → MyserviceApplication
  • boilerplate_db → myservice_db

계속 진행하시겠습니까? (Y/n): y
```

### 4. 자동 정리

설정 완료 후 setup 스크립트를 자동으로 삭제할 수 있습니다:

```
setup 스크립트를 삭제하시겠습니까? (Y/n): y

✓ setup-project.ps1 삭제됨
✓ setup-project.bat 삭제됨
✓ setup-project.sh 삭제됨

🎉 모든 설정이 완료되었습니다!
```

## 📋 변경되는 항목

| 항목 | 변경 전 | 변경 후 (예: myApp) |
|------|---------|---------------------|
| 기본 패키지 | `com.boilerplate` | `com.myapp` |
| 메인 클래스 | `BoilerplateApplication` | `MyappApplication` |
| 데이터베이스 | `boilerplate_db` | `myapp_db` |
| 프로젝트명 | `springboot-boilerplate` | `springboot-myapp` |
| 로깅 설정 | `com.boilerplate: DEBUG` | `com.myapp: DEBUG` |
| 캐시 설정 | `com.boilerplate.user.domain.User` | `com.myapp.user.domain.User` |

## ✅ 설정 후 확인사항

### 1. 데이터베이스 생성
```sql
-- PostgreSQL
CREATE DATABASE myapp_db;

-- 사용자 권한 설정 (필요시)
GRANT ALL PRIVILEGES ON DATABASE myapp_db TO postgres;
```

### 2. 환경 설정 파일 업데이트 (`src/main/resources/application.yml`)

프로젝트의 `application.yml` 또는 프로필별 설정 파일(`application-local.yml` 등)을 열어 다음 설정을 확인하거나 업데이트합니다.

#### JWT Secret Key 설정
```yaml
jwt:
  secret: {your-super-strong-and-long-jwt-secret-key}
  expiration: 86400000 # 24시간 (밀리초)
  refresh-expiration: 604800000 # 7일 (밀리초)
```

#### 파일 업로드 경로 및 허용 확장자 설정
```yaml
file:
  upload-dir: ./uploads # 파일이 저장될 기본 경로 (Docker 사용 시 볼륨 마운트 고려)
  allowed-extensions: ["jpg", "jpeg", "png", "gif", "pdf", "txt", "zip"] # 허용할 파일 확장자 목록. 비어있으면 모두 허용.
```

#### IP 화이트리스트 설정 (선택 사항)
```yaml
security:
  allowed-ips: [] # 허용할 IP 목록 (예: 127.0.0.1, 192.168.1.100). 비어있으면 모든 IP 허용.
```

### 3. 빌드 테스트

QueryDSL Q-Type 클래스 생성을 위해 `clean build`를 실행합니다.
```bash
# Gradle Wrapper 권한 부여 (Linux/Mac)
chmod +x gradlew

# 빌드 (QueryDSL Q-Type 생성 포함)
./gradlew clean build

# 성공 확인
# BUILD SUCCESSFUL 메시지 확인
```
> `build.gradle.kts`에 설정된 `testLogging`에 따라 성공, 실패, 스킵된 테스트가 상세하게 출력됩니다. 한글 테스트명도 깨지지 않고 정상적으로 표시됩니다.

### 4. 애플리케이션 실행
```bash
./gradlew bootRun
```

서버가 시작되면:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator/health

### 5. Git 설정 (선택사항)
```bash
# 새 저장소 초기화
git init

# 첫 커밋
git add .
git commit -m "Initial commit: Setup myApp project"

# 원격 저장소 연결
git remote add origin {project-url}
git push -u origin main
```

## 커스터마이징 옵션

### 패키지 구조 변경

기본값: `com.{프로젝트명}`

다른 구조를 원하는 경우:
```
프로젝트명: myService
패키지명: io.github.myusername.myservice
```

결과:
- `io/github/myusername/myservice/` 디렉토리 구조
- `package io.github.myusername.myservice;`

### 데이터베이스 이름 변경

기본값: `{프로젝트명}_db`

다른 이름을 원하는 경우:
```
DB 이름: service_production
```

## 문제 해결

### 빌드 오류 (QueryDSL 관련)

**증상:**
```
Cannot find symbol Q{EntityName}
```

**해결:**
QueryDSL Q-Type 클래스가 제대로 생성되지 않았을 수 있습니다. 다음 명령어로 Q-Type을 재생성하고 다시 빌드합니다.
```bash
./gradlew cleanQuerydsl # QueryDSL Q-Type 생성 디렉토리 삭제
./gradlew clean build    # Q-Type 재생성 및 전체 빌드
```

### 빌드 오류 (일반)

**증상:**
```
Could not find or load main class...
```

**해결:**
```bash
# Gradle 캐시 삭제
./gradlew clean

# IDE 캐시 무효화
# IntelliJ: File → Invalidate Caches / Restart
# Eclipse: Project → Clean

# 다시 빌드
./gradlew build
```

### 데이터베이스 연결 오류

**증상:**
```
Connection refused: localhost:5432
```

**확인사항:**
1. PostgreSQL이 실행 중인지 확인
2. `application.yml`의 DB 정보가 올바른지 확인
3. 데이터베이스가 생성되었는지 확인

```bash
# PostgreSQL 상태 확인 (Linux)
sudo systemctl status postgresql

# Windows
# services.msc에서 PostgreSQL 서비스 확인
```

## 설정 재실행

실수로 잘못 설정한 경우:

### Git을 사용하는 경우
```bash
# 변경사항 되돌리기
git checkout .
git clean -fd

# setup 스크립트 다시 실행
.\setup-project.ps1
```

### 환경별 설정

프로젝트는 3가지 프로필을 지원하며, `application.yml`에 공통 설정을, `application-{profile}.yml`에 프로필별 설정을 정의합니다.

```
application.yml           # 공통 설정
application-local.yml     # 로컬 개발 환경 (기본)
application-dev.yml       # 개발 서버 환경
application-prod.yml      # 프로덕션 환경
```

#### 프로필 실행 방법

```bash
# Local (기본값)
./gradlew bootRun

# Dev
./gradlew bootRun --args='--spring.profiles.active=dev'

# Prod
java -jar app.jar --spring.profiles.active=prod
```

#### 프로필별 차이점

| 설정 | local | dev | prod |
|------|-------|-----|------|
| ddl-auto | update | validate | validate |
| show-sql | true | false | false |
| 로그 레벨 | DEBUG | INFO | WARN |
| Swagger | 활성화 | 활성화 | 비활성화 |
| Actuator | 전체 노출 | 제한적 | 최소 |
