# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- **Member Domain:**
  - `Member` Entity, Repository, Service 구현.
  - 회원가입 API (`POST /api/v1/members/signup`) 구현.
  - 로그인 API (`POST /api/v1/members/login`) 및 JWT 발급 구현.
  - `MemberControllerTest` (Unit) 및 `MemberIntegrationTest` (Integration) 작성 및 통과.
- **Security:**
  - `JwtTokenProvider`: JWT 생성 및 검증 로직.
  - `JwtAuthenticationFilter`: JWT 인증 필터.
  - `SecurityConfig`: Spring Security 설정 (CSRF off, Stateless Session).
  - `CustomUserDetailsService`: DB 기반 사용자 인증 연동.
- **Project Setup:**
  - `build.gradle.kts`: OpenAPI Generator 설정 및 자동 빌드 구성.
  - `docker-compose.yml`: 로컬 개발용 PostgreSQL 컨테이너 설정.
  - `README.md`: DB 초기화 및 실행 가이드 작성.
- **Infrastructure:**
  - `application.yml`: Local(PostgreSQL) 및 Test(H2) 프로필 설정.
  - `V1__init_schema.sql`: 초기 DB 스키마(Member, Book, Order, Delivery) 작성.
  - Swagger UI 설정 (`/openapi.yaml` 연동).
