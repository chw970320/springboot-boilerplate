#!/usr/bin/env pwsh
# Spring Boot Boilerplate 프로젝트 초기 설정 스크립트 (PowerShell)
# 최초 1회 실행 후 자동 삭제됩니다.

$ErrorActionPreference = "Stop"

# --- 함수 정의 ---
function Write-ColorOutput($Color, $Message) {
    Write-Host $Message -ForegroundColor $Color
}

function Write-Header($Text) {
    Write-Output ""
    Write-ColorOutput Cyan "═══════════════════════════════════════════════════"
    Write-ColorOutput Cyan "  $Text"
    Write-ColorOutput Cyan "═══════════════════════════════════════════════════"
    Write-Output ""
}

function Write-Step($Number, $Total, $Description) {
    Write-ColorOutput Yellow "[$Number/$Total] $Description"
}

function Write-Success($Message) {
    Write-ColorOutput Green "✓ $Message"
}

function Write-Info($Message) {
    Write-ColorOutput Cyan "ℹ $Message"
}

function Write-ErrorMsg($Message) {
    Write-ColorOutput Red "❌ $Message"
}

# --- 스크립트 시작 ---
Clear-Host

Write-Header "Spring Boot Boilerplate 프로젝트 설정"

Write-Info "이 스크립트는 프로젝트를 커스터마이징하고 자동으로 삭제됩니다."
Write-Info "현재 설정: boilerplate → 새로운 프로젝트명"
Write-Output ""

# --- 기본값 설정 ---
$OldAppName = "boilerplate"
$OldAppNamePascal = "Boilerplate"
$OldBasePackage = "com.boilerplate"

# 1. 프로젝트명 입력
$NewAppNameInput = Read-Host -Prompt "프로젝트명을 입력하세요 (예: myService, userApi, blogApp)"
if ([string]::IsNullOrWhiteSpace($NewAppNameInput)) {
    Write-ErrorMsg "프로젝트명을 입력해야 합니다."; exit 1
}
$NewAppNameLower = ($NewAppNameInput.ToLower() -replace '\s', '')
$NewAppNamePascal = (Get-Culture).TextInfo.ToTitleCase($NewAppNameLower)
Write-Output ""

# 2. 패키지명 확인
$defaultPackage = "com.$NewAppNameLower"
$packageInput = Read-Host -Prompt "패키지명을 입력하세요 (엔터: $defaultPackage)"
$NewPackage = if ([string]::IsNullOrWhiteSpace($packageInput)) { $defaultPackage } else { $packageInput.ToLower() }
Write-Output ""

# 3. 데이터베이스명 확인
$defaultDbName = "${NewAppNameLower}_db"
$dbInput = Read-Host -Prompt "데이터베이스명을 입력하세요 (엔터: $defaultDbName)"
$NewDbName = if ([string]::IsNullOrWhiteSpace($dbInput)) { $defaultDbName } else { $dbInput }
Write-Output ""

# --- 변경사항 확인 ---
Write-Header "변경사항 확인"
Write-Host "  기존 패키지:    $OldBasePackage → $NewPackage"
Write-Host "  메인 클래스:    ${OldAppNamePascal}Application → ${NewAppNamePascal}Application"
Write-Host "  데이터베이스:   ${OldAppName}_db → $NewDbName"
Write-Host "  프로젝트명:     springboot-$OldAppName → springboot-$NewAppNameLower"
Write-Output ""

$confirm = Read-Host "계속 진행하시겠습니까? (Y/n)"
if ($confirm -match '^[nN]$') {
    Write-ColorOutput Yellow "설정이 취소되었습니다."; exit 0
}

Write-Header "프로젝트 설정 시작"

$totalSteps = 10
$currentStep = 0

# Step 1: 패키지 디렉토리 변경
$currentStep++
Write-Step $currentStep $totalSteps "패키지 디렉토리 변경 중..."

$newPackagePath = $NewPackage -replace '\.', '/'
$oldBaseSrcPath = "src/main/java/$OldBasePackage"
$newBaseSrcPath = "src/main/java/$newPackagePath"

# 기존 com.boilerplate 하위의 도메인 폴더들을 새 패키지 경로로 이동
$domainFolders = Get-ChildItem -Path $oldBaseSrcPath -Directory -Name
foreach ($folder in $domainFolders) {
    $sourcePath = Join-Path $oldBaseSrcPath $folder
    $destPath = Join-Path $newBaseSrcPath $folder
    
    New-Item -ItemType Directory -Path $destPath -Force | Out-Null
    Move-Item -Path "$sourcePath/*" -Destination $destPath -Force
    Write-Success "$folder 도메인 소스 이동 완료"
}

# BoilerplateApplication.java 이동
$oldAppFile = Join-Path $oldBaseSrcPath "BoilerplateApplication.java"
$newAppFile = Join-Path $newBaseSrcPath "${NewAppNamePascal}Application.java"
if (Test-Path $oldAppFile) {
    New-Item -ItemType Directory -Path (Split-Path $newAppFile) -Force | Out-Null
    Move-Item -Path $oldAppFile -Destination $newAppFile -Force
    Write-Success "BoilerplateApplication.java 이동 완료"
}

# 테스트 소스도 동일하게 이동
$oldTestBaseSrcPath = "src/test/java/$OldBasePackage"
$newTestBaseSrcPath = "src/test/java/$newPackagePath"
$testDomainFolders = Get-ChildItem -Path $oldTestBaseSrcPath -Directory -Name
foreach ($folder in $testDomainFolders) {
    $sourcePath = Join-Path $oldTestBaseSrcPath $folder
    $destPath = Join-Path $newTestBaseSrcPath $folder
    
    New-Item -ItemType Directory -Path $destPath -Force | Out-Null
    Move-Item -Path "$sourcePath/*" -Destination $destPath -Force
    Write-Success "$folder 테스트 소스 이동 완료"
}

# 빈 디렉토리 정리
Get-ChildItem -Path "src/main/java/com", "src/test/java/com" -Directory -Recurse | Where-Object { (Get-ChildItem $_.FullName).Count -eq 0 } | Remove-Item -Recurse -ErrorAction SilentlyContinue

# Step 2: 클래스 파일명 변경 (메인/테스트 Application 파일은 이미 이동 시 변경됨)
$currentStep++
Write-Step $currentStep $totalSteps "메인/테스트 클래스명 변경 확인..."
Write-Success "클래스 파일명은 이동 단계에서 처리되었습니다."

# Step 3: 프로젝트 전체 내용 변경
$currentStep++
Write-Step $currentStep $totalSteps "프로젝트 전체 내용 변경 중..."
$filesToProcess = Get-ChildItem -Path . -Recurse -File | Where-Object { 
    $_.FullName -notmatch "\.git" -and 
    $_.Name -notmatch "setup-project\.(sh|bat|ps1)" -and 
    $_.Name -notmatch "SETUP_GUIDE\.md" -and 
    $_.FullName -notmatch "build/generated/querydsl" 
}

foreach ($file in $filesToProcess) {
    $content = Get-Content -Path $file.FullName -Raw -Encoding UTF8
    $newContent = $content -replace $OldBasePackage, $NewPackage `
                              -replace "springboot-$OldAppName", "springboot-$NewAppNameLower" `
                              -replace "${OldAppName}_db", $NewDbName `
                              -replace "${OldAppNamePascal}Application", "${NewAppNamePascal}Application" `
                              -replace $OldAppNamePascal, $NewAppNamePascal `
                              -replace $OldAppName, $NewAppNameLower

    # QueryDSL Q-Type 경로 업데이트 (build.gradle.kts에서 사용)
    $newContent = $newContent -replace "srcDirs(\"src/main/java\", \"build/generated/querydsl\")", "srcDirs(\"src/main/java\", \"build/generated/querydsl\")"
    $newContent = $newContent -replace "com.boilerplate.api", "$NewPackage.api" # AOP 포인트컷 등
    $newContent = $newContent -replace "com.boilerplate.service", "$NewPackage.service"
    $newContent = $newContent -replace "com.boilerplate.mapper", "$NewPackage.mapper"
    $newContent = $newContent -replace "com.boilerplate.domain", "$NewPackage.domain"

    if ($newContent -ne $content) {
        $newContent | Set-Content -Path $file.FullName -Encoding UTF8 -NoNewline
    }
}
Write-Success "모든 관련 문자열 치환 완료"

# Step 4: build.gradle.kts 수정
$currentStep++
Write-Step $currentStep $totalSteps "build.gradle.kts 수정 중..."
$buildGradleContent = Get-Content "build.gradle.kts" -Raw -Encoding UTF8
$buildGradleContent = $buildGradleContent -replace "group = \"$OldBasePackage\"", "group = \"$NewPackage\""
$buildGradleContent = $buildGradleContent -replace "srcDirs(\"src/main/java\", \"build/generated/querydsl\")", "srcDirs(\"src/main/java\", \"build/generated/querydsl\")"
$buildGradleContent | Set-Content "build.gradle.kts" -Encoding UTF8 -NoNewline
Write-Success "build.gradle.kts 업데이트"

# Step 5: settings.gradle.kts 수정
$currentStep++
Write-Step $currentStep $totalSteps "settings.gradle.kts 수정 중..."
(Get-Content "settings.gradle.kts") -replace "rootProject.name = \"springboot-$OldAppName\"", "rootProject.name = \"springboot-$NewAppNameLower\"" | Set-Content "settings.gradle.kts" -Encoding UTF8 -NoNewline
Write-Success "settings.gradle.kts 업데이트"

# Step 6: Git 저장소 확인
$currentStep++
Write-Step $currentStep $totalSteps "Git 저장소 확인 중..."
if (Test-Path ".git") {
    Write-Info "기존 Git 저장소 발견 (유지됨)"
} else {
    $gitInit = Read-Host "새로운 Git 저장소를 초기화하시겠습니까? (y/N)"
    if ($gitInit -match '^[yY]$') {
        git init
        Write-Success "Git 저장소 초기화 완료"
    }
}

# Step 7: 완료 및 다음 단계 안내
$currentStep++
Write-Step $currentStep $totalSteps "완료 처리 중..."
Write-Header "설정 완료!"
Write-ColorOutput Green "✅ 프로젝트가 성공적으로 설정되었습니다!"
Write-Output ""
Write-Output "변경 요약:"
Write-Output "  • 패키지: $NewPackage"
Write-Output "  • 메인 클래스: ${NewAppNamePascal}Application"
Write-Output "  • 데이터베이스: $NewDbName"
Write-Output "  • 프로젝트명: springboot-$NewAppNameLower"
Write-Output ""
Write-ColorOutput Cyan "다음 단계:"
Write-Output "  1. 데이터베이스 생성: CREATE DATABASE $NewDbName;"
Write-Output "  2. 빌드 및 실행: ./gradlew clean build && ./gradlew bootRun"
Write-Output "  3. API 테스트: http://localhost:8080/swagger-ui.html"
Write-Output ""

# Step 8: 스크립트 자동 삭제
$currentStep++
Write-Step $currentStep $totalSteps "스크립트 자동 삭제 준비..."
$cleanup = Read-Host "setup 스크립트를 삭제하시겠습니까? (Y/n)"
if (-not ($cleanup -match '^[nN]$')) {
    Write-Output ""
    Write-ColorOutput Yellow "정리 중..."
    Get-Item -Path "setup-project.ps1", "setup-project.bat", "setup-project.sh", "SETUP_GUIDE.md" -ErrorAction SilentlyContinue | Remove-Item -Force
    Write-Success "관련 설정 파일 삭제 완료"
    Write-Output ""
    Write-ColorOutput Green "🎉 모든 설정이 완료되었습니다! 이제 깔끔한 프로젝트로 개발을 시작하세요!"
} else {
    Write-Output ""
    Write-ColorOutput Yellow "setup 스크립트가 유지됩니다."
    Write-ColorOutput Cyan "나중에 수동으로 삭제하세요: Remove-Item setup-project.*"
}

Write-Output ""
