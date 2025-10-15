#!/usr/bin/env pwsh
# Spring Boot Boilerplate 프로젝트 초기 설정 스크립트 (PowerShell)
# 최초 1회 실행 후 자동 삭제됩니다.

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8 # Git Bash/터미널 호환성용 인코딩 설정

# --- 함수 정의 ---
# 터미널 호환성을 위해 ASCII 문자로만 출력
function Write-Header($Text) {
    Write-Output ""
    Write-Output "==========================================================="
    Write-Output "  $Text"
    Write-Output "==========================================================="
    Write-Output ""
}

function Write-Step($Number, $Total, $Description) {
    Write-Output "[$Number/$Total] $Description"
}

function Write-Success($Message) {
    Write-Output "[OK] $Message"
}

function Write-Info($Message) {
    Write-Output "[INFO] $Message"
}

function Write-ErrorMsg($Message) {
    Write-Output "[ERROR] $Message"
}

# --- 스크립트 시작 ---
if ($IsWindows) { Clear-Host } else { Clear-Host }

Write-Header "Spring Boot Boilerplate Project Setup"

Write-Info "This script will customize the project and then delete itself."
Write-Info "Current setting: boilerplate -> new project name"
Write-Output ""

# --- 기본값 설정 ---
$OldAppName = "boilerplate"
$OldAppNamePascal = "Boilerplate"
$OldBasePackage = "com.boilerplate"
$EscapedOldBasePackage = [regex]::Escape($OldBasePackage)

# 1. 프로젝트명 입력
$NewAppNameInput = Read-Host -Prompt "Enter the project name (e.g., myService, userApi, blogApp)"
if ([string]::IsNullOrWhiteSpace($NewAppNameInput)) {
    Write-ErrorMsg "Project name is required."; exit 1
}
$NewAppNameLower = ($NewAppNameInput.ToLower() -replace '[\s-]', '')
$NewAppNamePascal = (Get-Culture).TextInfo.ToTitleCase($NewAppNameLower)

# 2. 패키지명 확인
$defaultPackage = "com.$NewAppNameLower"
$packageInput = Read-Host -Prompt "Enter the package name (press Enter for: $defaultPackage)"
$NewPackage = if ([string]::IsNullOrWhiteSpace($packageInput)) { $defaultPackage } else { $packageInput.ToLower() }
Write-Output ""

# 3. 데이터베이스명 확인
$defaultDbName = "${NewAppNameLower}_db"
$dbInput = Read-Host -Prompt "Enter the database name (press Enter for: $defaultDbName)"
$NewDbName = if ([string]::IsNullOrWhiteSpace($dbInput)) { $defaultDbName } else { $dbInput }
Write-Output ""

# --- 변경사항 확인 ---
Write-Header "Review Changes"
Write-Output "  Old Package:    $OldBasePackage -> $NewPackage"
Write-Output "  Main Class:     ${OldAppNamePascal}Application -> ${NewAppNamePascal}Application"
Write-Output "  Database:       ${OldAppName}_db -> $NewDbName"
Write-Output "  Project Name:   springboot-$OldAppName -> springboot-$NewAppNameLower"
Write-Output ""

$confirm = Read-Host "Do you want to continue? (Y/n)"
if ($confirm -match '^[nN]$') {
    Write-Output "Setup canceled."; exit 0
}

Write-Header "Starting Project Setup"

$totalSteps = 6
$currentStep = 0

# Step 1: 패키지 디렉토리 이동
$currentStep++
Write-Step $currentStep $totalSteps "Moving package directories..."

$oldPackagePath = $OldBasePackage -replace '\.', '/'
$newPackagePath = $NewPackage -replace '\.', '/'

# 메인 소스 디렉토리 이동
$oldBaseSrcPath = "src/main/java/$oldPackagePath"
$newBaseSrcPath = "src/main/java/$newPackagePath"
if (Test-Path $oldBaseSrcPath) {
    $parentDir = Split-Path -Path $newBaseSrcPath -Parent
    if (-not (Test-Path $parentDir)) { New-Item -ItemType Directory -Path $parentDir -Force | Out-Null }
    Move-Item -Path $oldBaseSrcPath -Destination $newBaseSrcPath -Force
    Write-Success "Moved main source directory."
}

# 테스트 소스 디렉토리 이동
$oldTestBaseSrcPath = "src/test/java/$oldPackagePath"
$newTestBaseSrcPath = "src/test/java/$newPackagePath"
if (Test-Path $oldTestBaseSrcPath) {
    $parentDir = Split-Path -Path $newTestBaseSrcPath -Parent
    if (-not (Test-Path $parentDir)) { New-Item -ItemType Directory -Path $parentDir -Force | Out-Null }
    Move-Item -Path $oldTestBaseSrcPath -Destination $newTestBaseSrcPath -Force
    Write-Success "Moved test source directory."
}

# Step 2: 애플리케이션 파일 이름 변경
$currentStep++
Write-Step $currentStep $totalSteps "Renaming application files..."

$mainAppFile = Join-Path $newBaseSrcPath "BoilerplateApplication.java"
if (Test-Path $mainAppFile) {
    Rename-Item -Path $mainAppFile -NewName "${NewAppNamePascal}Application.java" -Force
    Write-Success "Renamed main application file."
}

$testAppFile = Join-Path $newTestBaseSrcPath "BoilerplateApplicationTests.java"
if (Test-Path $testAppFile) {
    Rename-Item -Path $testAppFile -NewName "${NewAppNamePascal}ApplicationTests.java" -Force
    Write-Success "Renamed test application file."
}

# Step 3: 프로젝트 전체 내용 변경
$currentStep++
Write-Step $currentStep $totalSteps "Replacing content across the project..."
$filesToProcess = Get-ChildItem -Path . -Recurse -File | Where-Object { 
    $_.FullName -notmatch '\\.git' -and 
    $_.Name -notmatch 'setup-project\.(sh|bat|ps1)' -and 
    $_.Name -notmatch 'SETUP_GUIDE\.md' -and
    $_.FullName -notmatch '[\\/]build[\\/]generated[\\/]querydsl'
}

foreach ($file in $filesToProcess) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName)
        # 가장 구체적인 것부터 교체 (예: BoilerplateApplicationTests -> BoilerplateApplication -> Boilerplate)
        $newContent = $content -replace "${OldAppNamePascal}ApplicationTests", "${NewAppNamePascal}ApplicationTests" `
                                  -replace "${OldAppNamePascal}Application", "${NewAppNamePascal}Application" `
                                  -replace $EscapedOldBasePackage, $NewPackage `
                                  -replace "springboot-$OldAppName", "springboot-$NewAppNameLower" `
                                  -replace "${OldAppName}_db", $NewDbName `
                                  -replace $OldAppNamePascal, $NewAppNamePascal `
                                  -replace $OldAppName, $NewAppNameLower

        if ($newContent -ne $content) {
            [System.IO.File]::WriteAllText($file.FullName, $newContent, [System.Text.UTF8Encoding]::new($false))
        }
    } catch {
        Write-ErrorMsg "Error processing file: $($file.FullName) - $($_.Exception.Message)"
    }
}
Write-Success "Replaced all relevant strings."

# Step 4: Gradle 파일 수정
$currentStep++
Write-Step $currentStep $totalSteps "Updating Gradle files..."

$buildGradlePath = "build.gradle.kts"
$buildGradleContent = [System.IO.File]::ReadAllText($buildGradlePath)
$oldGroupString = 'group = "' + $OldBasePackage + '"'
$newGroupString = 'group = "' + $NewPackage + '"'
$buildGradleContent = $buildGradleContent.Replace($oldGroupString, $newGroupString)
[System.IO.File]::WriteAllText($buildGradlePath, $buildGradleContent, [System.Text.UTF8Encoding]::new($false))
Write-Success "build.gradle.kts updated."

$settingsGradlePath = "settings.gradle.kts"
$settingsGradleContent = [System.IO.File]::ReadAllText($settingsGradlePath)
$oldRootProjectString = 'rootProject.name = "springboot-' + $OldAppName + '"'
$newRootProjectString = 'rootProject.name = "springboot-' + $NewAppNameLower + '"'
$settingsGradleContent = $settingsGradleContent.Replace($oldRootProjectString, $newRootProjectString)
[System.IO.File]::WriteAllText($settingsGradlePath, $settingsGradleContent, [System.Text.UTF8Encoding]::new($false))
Write-Success "settings.gradle.kts updated."

# Step 5: Git 저장소 확인
$currentStep++
Write-Step $currentStep $totalSteps "Checking Git repository..."
if (Test-Path ".git") {
    Write-Info "Existing Git repository found (will be kept)."
} else {
    $gitInit = Read-Host "Initialize a new Git repository? (y/N)"
    if ($gitInit -match '^[yY]$') {
        git init
        Write-Success "Git repository initialized."
    }
}

# Step 6: 완료 및 다음 단계 안내
$currentStep++
Write-Step $currentStep $totalSteps "Finalizing setup..."
Write-Header "Setup Complete!"
Write-Output "Project has been set up successfully!"
Write-Output ""
Write-Output "Summary of changes:"
Write-Output "  * Package: $NewPackage"
Write-Output "  * Main Class: ${NewAppNamePascal}Application"
Write-Output "  * Database: $NewDbName"
Write-Output "  * Project Name: springboot-$NewAppNameLower"
Write-Output ""
Write-Output "Next Steps:"
Write-Output "  1. Create the database: CREATE DATABASE $NewDbName;"
Write-Output "  2. Build and run: ./gradlew clean build && ./gradlew bootRun"
Write-Output "  3. Test the API: http://localhost:8080/swagger-ui.html"
Write-Output ""

# 스크립트 자동 삭제
$cleanup = Read-Host "Delete setup scripts? (Y/n)"
if (-not ($cleanup -match '^[nN]$')) {
    Write-Output ""
    Write-Info "Cleaning up..."
    Get-Item -Path "setup-project.ps1", "setup-project.bat", "setup-project.sh", "SETUP_GUIDE.md" -ErrorAction SilentlyContinue | Remove-Item -Force
    Write-Success "Related setup files deleted."
    Write-Output ""
    Write-Output "All set! Happy coding!"
} else {
    Write-Output ""
    Write-Info "Setup scripts will be kept."
    Write-Info "Please delete them manually later: Remove-Item setup-project.*"
}

Write-Output ""
