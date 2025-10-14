#!/bin/bash
# Spring Boot Boilerplate 프로젝트 초기 설정 (Linux/Mac)
# 최초 1회 실행 후 자동 삭제됩니다.

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# 함수 정의
print_header() {
    echo ""
    echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
    echo -e "${CYAN}  $1${NC}"
    echo -e "${CYAN}═══════════════════════════════════════════════════${NC}"
    echo ""
}

print_step() {
    echo -e "${YELLOW}[$1/$2] $3${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# sed 명령어 크로스플랫폼 호환성 처리
sedi() {
    if [[ "$(uname)" == "Darwin" ]]; then
        sed -i "" "$@"
    else
        sed -i "$@"
    fi
}

clear

print_header "Spring Boot Boilerplate 프로젝트 설정"

print_info "이 스크립트는 프로젝트를 커스터마이징하고 자동으로 삭제됩니다."
print_info "현재 설정: boilerplate → 새로운 프로젝트명"
echo ""

# 기본값
OLD_APP_NAME="boilerplate"
OLD_APP_NAME_PASCAL="Boilerplate"

# 1. 프로젝트명 입력
echo -e "${WHITE}프로젝트명을 입력하세요 (예: myService, userApi, blogApp)${NC}"
read -p "프로젝트명: " NEW_APP_NAME_INPUT

if [ -z "$NEW_APP_NAME_INPUT" ]; then
    print_error "프로젝트명을 입력해야 합니다."
    exit 1
fi

NEW_APP_NAME_LOWER=$(echo "$NEW_APP_NAME_INPUT" | tr '[:upper:]' '[:lower:]' | tr -d ' ')
NEW_APP_NAME_PASCAL=$(echo "$NEW_APP_NAME_LOWER" | sed 's/\b\(.\)/\u\1/g')

echo ""

# 2. 패키지명 확인
DEFAULT_PACKAGE="com.$NEW_APP_NAME_LOWER"
echo -e "${WHITE}패키지명을 입력하세요 (엔터: $DEFAULT_PACKAGE)${NC}"
read -p "패키지명: " PACKAGE_INPUT

if [ -z "$PACKAGE_INPUT" ]; then
    NEW_PACKAGE="$DEFAULT_PACKAGE"
else
    NEW_PACKAGE=$(echo "$PACKAGE_INPUT" | tr '[:upper:]' '[:lower:]')
fi

echo ""

# 3. 데이터베이스명 확인
DEFAULT_DB_NAME="${NEW_APP_NAME_LOWER}_db"
echo -e "${WHITE}데이터베이스명을 입력하세요 (엔터: $DEFAULT_DB_NAME)${NC}"
read -p "DB 이름: " DB_INPUT

if [ -z "$DB_INPUT" ]; then
    NEW_DB_NAME="$DEFAULT_DB_NAME"
else
    NEW_DB_NAME="$DB_INPUT"
fi

echo ""
echo ""

# 변경사항 요약
print_header "변경사항 확인"
echo "  패키지:        com.$OLD_APP_NAME → $NEW_PACKAGE"
echo "  메인 클래스:    ${OLD_APP_NAME_PASCAL}Application → ${NEW_APP_NAME_PASCAL}Application"
echo "  데이터베이스:   ${OLD_APP_NAME}_db → $NEW_DB_NAME"
echo "  프로젝트명:     springboot-$OLD_APP_NAME → springboot-$NEW_APP_NAME_LOWER"
echo ""

read -p "계속 진행하시겠습니까? (Y/n): " CONFIRM
if [ "$CONFIRM" = "n" ] || [ "$CONFIRM" = "N" ]; then
    echo -e "${YELLOW}설정이 취소되었습니다.${NC}"
    exit 0
fi

echo ""
print_header "프로젝트 설정 시작"

TOTAL_STEPS=9 # Dockerfile 수정 단계 제거로 총 단계 수 변경
CURRENT_STEP=0

# Step 1: 패키지 디렉토리 변경
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "패키지 디렉토리 변경 중..."

NEW_PACKAGE_PATH=$(echo "$NEW_PACKAGE" | tr '.' '/')

if [ -d "src/main/java/com/$OLD_APP_NAME" ]; then
    mkdir -p "src/main/java/$NEW_PACKAGE_PATH"
    mv src/main/java/com/$OLD_APP_NAME/* "src/main/java/$NEW_PACKAGE_PATH/" 2>/dev/null || true
    print_success "Main 소스 이동 완료"
fi

if [ -d "src/test/java/com/$OLD_APP_NAME" ]; then
    mkdir -p "src/test/java/$NEW_PACKAGE_PATH"
    mv src/test/java/com/$OLD_APP_NAME/* "src/test/java/$NEW_PACKAGE_PATH/" 2>/dev/null || true
    print_success "Test 소스 이동 완료"
fi

# 빈 디렉토리 정리
find src/main/java/com -type d -empty -delete 2>/dev/null || true
find src/test/java/com -type d -empty -delete 2>/dev/null || true

# Step 2: 클래스 파일명 변경
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "메인/테스트 클래스명 변경 중..."

OLD_MAIN_FILE="src/main/java/$NEW_PACKAGE_PATH/${OLD_APP_NAME_PASCAL}Application.java"
NEW_MAIN_FILE="src/main/java/$NEW_PACKAGE_PATH/${NEW_APP_NAME_PASCAL}Application.java"
if [ -f "$OLD_MAIN_FILE" ]; then
    mv "$OLD_MAIN_FILE" "$NEW_MAIN_FILE"
    print_success "${OLD_APP_NAME_PASCAL}Application.java → ${NEW_APP_NAME_PASCAL}Application.java"
fi

OLD_TEST_FILE="src/test/java/$NEW_PACKAGE_PATH/${OLD_APP_NAME_PASCAL}ApplicationTests.java"
NEW_TEST_FILE="src/test/java/$NEW_PACKAGE_PATH/${NEW_APP_NAME_PASCAL}ApplicationTests.java"
if [ -f "$OLD_TEST_FILE" ]; then
    mv "$OLD_TEST_FILE" "$NEW_TEST_FILE"
    print_success "${OLD_APP_NAME_PASCAL}ApplicationTests.java → ${NEW_APP_NAME_PASCAL}ApplicationTests.java"
}

# Step 3: 프로젝트 전체 내용 변경
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "프로젝트 전체 내용 변경 중..."

# 모든 텍스트 기반 파일을 대상으로 변경
# --exclude-dir=".git" --exclude="setup-project.*" --exclude="SETUP_GUIDE.md" --exclude="build/generated/querydsl" 추가
FILES=$(grep -rl --exclude-dir=".git" --exclude="setup-project.*" --exclude="SETUP_GUIDE.md" --exclude-dir="build/generated/querydsl" "boilerplate" . || true)

for file in $FILES; do
    sedi -e "s/com\.$OLD_APP_NAME/$NEW_PACKAGE/g" \
         -e "s/springboot-$OLD_APP_NAME/springboot-$NEW_APP_NAME_LOWER/g" \
         -e "s/${OLD_APP_NAME}_db/$NEW_DB_NAME/g" \
         -e "s/${OLD_APP_NAME_PASCAL}Application/${NEW_APP_NAME_PASCAL}Application/g" \
         -e "s/$OLD_APP_NAME_PASCAL/$NEW_APP_NAME_PASCAL/g" \
         -e "s/$OLD_APP_NAME/$NEW_APP_NAME_LOWER/g" \
         "$file"
done
print_success "Boilerplate 문자열 치환 완료"

# Step 4: build.gradle.kts 수정
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "build.gradle.kts 수정 중..."
sedi "s/group = \"com.boilerplate\"/group = \"$NEW_PACKAGE\"/g" build.gradle.kts
print_success "build.gradle.kts 업데이트"

# Step 5: settings.gradle.kts 수정
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "settings.gradle.kts 수정 중..."
sedi "s/rootProject.name = \"springboot-boilerplate\"/rootProject.name = \"springboot-$NEW_APP_NAME_LOWER\"/g" settings.gradle.kts
print_success "settings.gradle.kts 업데이트"

# Step 6: application.yml 수정
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "application.yml 수정 중..."
sedi -e "s/name: springboot-boilerplate/name: springboot-$NEW_APP_NAME_LOWER/g" \
     -e "s/boilerplate_db/$NEW_DB_NAME/g" \
     src/main/resources/application.yml
print_success "application.yml 업데이트"

# Step 7: README.md 수정
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "README.md 수정 중..."
sedi -e "s/springboot-boilerplate/springboot-$NEW_APP_NAME_LOWER/g" \
     -e "s/com.boilerplate/$NEW_PACKAGE/g" \
     -e "s/boilerplate_db/$NEW_DB_NAME/g" \
     README.md
print_success "README.md 업데이트"

# Step 8: Git 저장소 확인
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "Git 저장소 확인 중..."

if [ -d ".git" ]; then
    print_info "기존 Git 저장소 발견 (유지됨)"
else
    echo ""
    read -p "새로운 Git 저장소를 초기화하시겠습니까? (y/N): " GIT_INIT
    if [ "$GIT_INIT" = "y" ] || [ "$GIT_INIT" = "Y" ]; then
        git init
        print_success "Git 저장소 초기화 완료"
    fi
fi

# Step 9: 스크립트 자동 삭제
CURRENT_STEP=$((CURRENT_STEP+1))
print_step $CURRENT_STEP $TOTAL_STEPS "스크립트 자동 삭제 준비..."

echo ""
print_header "설정 완료!"

echo ""
echo -e "${GREEN}✅ 프로젝트가 성공적으로 설정되었습니다!${NC}"
echo ""
echo "변경 요약:"
echo "  • 패키지: $NEW_PACKAGE"
echo "  • 메인 클래스: ${NEW_APP_NAME_PASCAL}Application"
echo "  • 데이터베이스: $NEW_DB_NAME"
echo "  • 프로젝트명: springboot-$NEW_APP_NAME_LOWER"
echo ""
echo -e "${CYAN}다음 단계:${NC}"
echo "  1. 데이터베이스 생성:"
echo "     CREATE DATABASE $NEW_DB_NAME;"
echo ""
echo "  2. 빌드 및 실행:"
echo "     ./gradlew build"
echo "     ./gradlew bootRun"
echo ""
echo "  3. API 테스트:"
echo "     http://localhost:8080/swagger-ui.html"
echo ""

read -p "setup 스크립트를 삭제하시겠습니까? (Y/n): " CLEANUP

if [ "$CLEANUP" != "n" ] && [ "$CLEANUP" != "N" ]; then
    echo ""
    echo -e "${YELLOW}정리 중...${NC}"
    
    FILES_TO_DELETE="setup-project.ps1 setup-project.bat setup-project.sh SETUP_GUIDE.md"
    
    for file in $FILES_TO_DELETE; do
        if [ -f "$file" ]; then
            rm -f "$file"
            print_success "$file 삭제됨"
        fi
    done
    
    echo ""
    echo -e "${GREEN}🎉 모든 설정이 완료되었습니다!${NC}"
    echo -e "${GREEN}이제 깔끔한 프로젝트로 개발을 시작하세요!${NC}"
else
    echo ""
    echo -e "${YELLOW}setup 스크립트가 유지됩니다.${NC}"
    echo -e "${CYAN}나중에 수동으로 삭제하세요: rm setup-project.*${NC}"
fi

echo ""
