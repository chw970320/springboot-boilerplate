-- V1__init.sql
-- 초기 데이터베이스 스키마 생성

-- users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,                          -- 사용자 고유 ID (자동 증가)
    username VARCHAR(50) NOT NULL UNIQUE,               -- 로그인 ID (중복 불가)
    email VARCHAR(100) NOT NULL UNIQUE,                 -- 이메일 주소 (중복 불가)
    password VARCHAR(255) NOT NULL,                     -- 암호화된 비밀번호 (BCrypt)
    first_name VARCHAR(50),                             -- 이름
    last_name VARCHAR(50),                              -- 성
    role VARCHAR(20) NOT NULL DEFAULT 'USER',           -- 사용자 권한 (USER, ADMIN)
    enabled BOOLEAN NOT NULL DEFAULT TRUE,              -- 계정 활성화 상태
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 생성 일시
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP   -- 수정 일시
);

-- users 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 방문자 상세 이력 테이블 (감사 로그용)
CREATE TABLE IF NOT EXISTS visitor_history (
    id BIGSERIAL PRIMARY KEY,                          -- 이력 고유 ID
    url VARCHAR(255) NOT NULL,                          -- 요청 URL
    method VARCHAR(10) NOT NULL,                        -- HTTP 메서드
    ip_address VARCHAR(50),                             -- 클라이언트 IP 주소
    user_agent TEXT,                                    -- 클라이언트 User-Agent
    username VARCHAR(50),                               -- 로그인한 사용자명 (nullable)
    visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 방문 일시
);

-- visitor_history 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_visitor_history_visited_at ON visitor_history(visited_at);
CREATE INDEX IF NOT EXISTS idx_visitor_history_username ON visitor_history(username);

-- 일일 순수 방문자(DAU) 집계 테이블
CREATE TABLE IF NOT EXISTS daily_unique_visitor (
    id BIGSERIAL PRIMARY KEY,
    visit_date DATE NOT NULL,                           -- 방문 날짜
    visitor_id VARCHAR(255) NOT NULL,                   -- 방문자 식별자 (username 또는 쿠키 UUID)
    visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 최초 방문 시간
);

-- 동일 날짜, 동일 방문자의 중복 저장을 막기 위한 UNIQUE 인덱스
CREATE UNIQUE INDEX IF NOT EXISTS uidx_daily_visitor_date_id ON daily_unique_visitor(visit_date, visitor_id);

-- 메뉴별 사용 통계 집계 테이블
CREATE TABLE IF NOT EXISTS menu_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,                    -- 메뉴 식별자
    visit_date DATE NOT NULL,                           -- 방문 날짜
    username VARCHAR(50) NOT NULL,                      -- 사용자 식별자 (비로그인 시 'anonymous')
    usage_count INT NOT NULL DEFAULT 1                  -- 일일 사용 횟수
);

-- UPSERT를 위한 UNIQUE 인덱스
CREATE UNIQUE INDEX IF NOT EXISTS uidx_menu_stats_key ON menu_usage_stats(menu_name, visit_date, username);

-- 파일 첨부 정보 테이블
CREATE TABLE IF NOT EXISTS file_attachments (
    id BIGSERIAL PRIMARY KEY,                           -- 파일 고유 ID
    file_name VARCHAR(255) NOT NULL,                    -- 원본 파일명
    stored_file_name VARCHAR(255) NOT NULL UNIQUE,      -- 서버에 저장된 파일명 (중복 방지)
    file_type VARCHAR(100),                             -- 파일 MIME 타입
    extension VARCHAR(20),                              -- 파일 확장자
    file_size BIGINT NOT NULL,                          -- 파일 크기 (bytes)
    upload_dir VARCHAR(255) NOT NULL,                   -- 저장된 디렉토리 경로
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 업로드 일시
);

-- 저장된 파일명으로 빠르게 조회하기 위한 인덱스
CREATE INDEX IF NOT EXISTS idx_stored_file_name ON file_attachments(stored_file_name);

-- 테이블 및 컬럼 주석 추가
COMMENT ON TABLE daily_unique_visitor IS '일일 순수 방문자(DAU) 집계 테이블';
COMMENT ON TABLE menu_usage_stats IS '메뉴별 일일 사용 통계 집계 테이블';
COMMENT ON TABLE file_attachments IS '업로드된 파일 첨부 정보 테이블';
