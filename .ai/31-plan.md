# Phase 1 Implementation Plan: Foundation & Member Domain

**목표:** 프로젝트 기본 환경(DB, Swagger, Code Gen)을 구축하고, 회원 가입 및 로그인 기능을 완성한다.

## 1. Project Setup & Infrastructure
- [ ] **OpenAPI Generator 설정 검증**
  - `gradlew openApiGenerate` 실행하여 `build/generated`에 코드 생성 확인.
  - 생성된 코드가 소스셋에 잘 포함되는지 확인.
- [ ] **Swagger UI 정적 파일 설정**
  - `openapi/openapi.yaml` -> `src/main/resources/static/openapi.yaml` 복사.
  - `application.properties`에 `springdoc.swagger-ui.url=/openapi.yaml` 설정.
  - 서버 실행 후 Swagger UI 접속 테스트.
- [ ] **Database & Flyway 설정**
  - `src/main/resources/db/migration/V1__init_schema.sql` 작성 (Member, Book, Order, Delivery 테이블).
  - H2(Local) 및 PostgreSQL(Prod) 연결 설정.

## 2. Member Domain Implementation
### 2.1 회원가입 (Signup)
- [ ] **API Spec & Test (Red)**
  - `MemberApiDelegate` 인터페이스 확인.
  - `MemberControllerTest` 작성 (회원가입 성공/실패 케이스).
- [ ] **Implementation (Green)**
  - `Member` Entity 및 `MemberRepository` 생성.
  - `MemberService` (Delegate 구현체) 작성.
  - PasswordEncoder (BCrypt) 적용.
- [ ] **Refactoring**
  - 예외 처리 (이메일 중복 등) 및 공통 에러 응답 적용.

### 2.2 로그인 & 인증 (Login & Auth)
- [ ] **JWT Util 구현**
  - Token 생성 및 검증 로직 (`JwtTokenProvider`).
- [ ] **Spring Security 설정**
  - `SecurityConfig` 작성 (CSRF 비활성화, 세션 미사용, 필터 등록).
  - `JwtAuthenticationFilter` 구현.
- [ ] **로그인 API 구현**
  - `MemberApiDelegate.login()` 구현.
  - 로그인 성공 시 Access Token 반환.

## 3. Verification
- [ ] **Integration Test**
  - 회원가입 -> 로그인 -> 토큰 발급 시나리오 테스트.
- [ ] **Swagger UI 확인**
  - 정적 파일 기반 Swagger UI에서 API 명세 확인.
