# Changelog

All notable changes to this project will be documented in this file.

## [0.5.0] - 2026-02-19
### Added
- **Monitoring & Logging:**
  - `spring-boot-starter-actuator` 의존성 추가 및 `health`, `info`, `metrics` 엔드포인트 활성화.
  - `logback-spring.xml` 설정: Local/Test(Console), Prod(File JSON) 환경 분리.
  - `net.logstash.logback:logstash-logback-encoder` 의존성 추가 (JSON 로그 포맷).
  - 주요 비즈니스 로직(`OrderService`, `PaymentService`) 및 예외 처리(`GlobalExceptionHandler`)에 로깅 추가.
- **Tests:**
  - `OrderControllerTest`: 재고 부족, 결제 실패, 타인 주문 취소 시도에 대한 엣지 케이스 테스트 추가.
  - `MemberControllerTest`: 중복 이메일 가입, 로그인 실패/성공에 대한 테스트 추가.
  - `E2EIntegrationTest`: 회원가입부터 배송 조회까지의 전체 시나리오 통합 테스트 검증 완료.

## [0.6.0] - 2026-02-20

### Added
- **Security & Administration:**
  - Keycloak Integration 계획 수립.
  - Spring Security OAuth2 Resource Server 설정 계획.
  - 관리자 API 및 Audit Logging 계획 수립.

### Changed
- **Refactoring:**
  - `Book` Entity: 재고 부족 시 `IllegalStateException` 대신 `OutOfStockException`을 던지도록 수정.
  - `SecurityConfig`: Actuator 엔드포인트(`/actuator/**`) 접근 허용 추가.

### Fixed
- **Infrastructure:**
  - `docker-compose.yml`: Keycloak 서비스 추가 (누락된 설정 복구).
- **Audit Logging:**
  - `AdminAuditLog` Entity 및 Repository 구현.
  - AOP 기반 감사 로그 시스템 (`@AuditLog`, `AuditLogAspect`) 구현.
  - `AdminApiDelegateImpl`의 조회 메서드에 감사 로그 적용.

## [Unreleased]

### Added
- **Security & Administration:**
  - `AdminApiDelegateImpl`: 관리자 도서/회원/주문 관리 엔드포인트(`createBook`, `updateBook`, `deleteBook`, `deleteMember`, `cancelOrderAdmin`) 구현.
  - `AdminApiDelegateImpl`: `@PreAuthorize("hasRole('ADMIN')")` 및 `@AuditLog` 액션 확장 적용.
  - `AdminControllerTest`: 관리자 API 통합 시나리오(관리자 접근 성공, 일반 사용자 403, 관리자 CRUD/강제취소) 추가.
  - `LoginTokenIssuer` 도입: `/members/login` 토큰 발급을 Keycloak 위임(`KeycloakLoginTokenIssuer`)으로 전환하고 테스트 전용 스텁(`TestLoginTokenIssuer`) 추가.
  - Keycloak 로컬 Realm/Client/User 구성 후 `verify-keycloak-e2e.ps1` 실행으로 실토큰 인증/권한 검증 통과.
- **Documentation:**
  - `docs/system-settings/keycloak-live-verification.md`: Keycloak 실토큰 기반 인증/인가 점검 절차 추가.
  - `30-roadmap.md` / `31-plan.md` / `32-todo.md`: Phase 6 진행상태 동기화 (`Admin API 완료`, `Keycloak 마이그레이션 정리/실토큰 검증 대기`).
  - `02-coding-conventions.md`: Exception Handling 규칙 추가.
  - `20-architecture.md`: Cross-Cutting Concerns (Exception Handling) 섹션 추가.
  - `openapi.yaml`: 에러 코드 테이블을 `<details>` 태그로 감싸서 가독성 개선.
- **OpenAPI & Swagger:**
  - OpenAPI SSOT 구축 (정적 파일 로드, 경로 변경).
  - Swagger UI 경로를 `/api/v1/swagger-ui.html`로 변경.
  - `SecurityConfig` 및 `WebConfig` 설정 개선.
  - OpenAPI `bearerAuth`를 Keycloak OAuth2 Authorization Code Flow로 전환하여 Swagger `Authorize` 버튼에서 OAuth 로그인 지원.
  - `application.yml`에 `springdoc.swagger-ui.oauth` 설정 추가 (`client-id`, PKCE, scopes).
  - Swagger OAuth용 Public Client 분리(`intellian-app-angular-client`) 및 `client-secret` 제거.
- **Delivery Domain:**
  - `Delivery` Entity 및 Repository 구현.
  - 배송 생성 API (`POST /api/v1/deliveries`) 구현.
  - 배송 상태 조회 API (`GET /api/v1/deliveries/{deliveryId}`) 구현.
  - `DeliveryControllerTest`: 배송 생성 및 조회 테스트 작성.
  - `E2EIntegrationTest`: 회원가입부터 배송 조회까지 전체 시나리오 검증.
- **Order Domain:**
  - `Order`, `OrderItem` Entity 및 Repository 구현.
  - 주문 생성 API (`POST /api/v1/orders`) 구현 (재고 차감, 결제 연동).
  - 주문 목록 조회 API (`GET /api/v1/orders`) 구현.
  - 주문 취소 API (`POST /api/v1/orders/{orderId}/cancel`) 구현 (재고 복구, 결제 취소).
  - `MockPaymentService`: 결제 승인/취소 모의 구현.
  - `OrderControllerTest`: 주문 생성, 조회, 취소 테스트 작성.
  - `OrderIntegrationTest`: 전체 주문 프로세스 통합 테스트 작성.
- **Catalog Domain:**
  - `Book` Entity, Repository, Service 구현.
  - 도서 목록 조회 API (`GET /api/v1/books`) 구현 (페이징, 검색).
  - 도서 상세 조회 API (`GET /api/v1/books/{bookId}`) 구현.
  - `CatalogControllerTest` 작성 및 통과.
  - `V2__insert_sample_books.sql`: 초기 샘플 도서 데이터(5권) 추가.
  - `CatalogIntegrationTest`: 도서 목록 및 상세 조회 통합 테스트 작성.
- **Refactoring:**
  - `MemberService`: `JwtTokenProvider` 의존 제거, `LoginTokenIssuer` 기반으로 로그인 흐름 전환.
  - 레거시 JWT 구성 정리: `JwtTokenProvider`, `JwtAuthenticationFilter`, `CustomUserDetailsService` 삭제 및 `jjwt` 의존성 제거.
  - `SecurityConfig`: Keycloak 토큰의 principal claim을 `preferred_username`으로 고정.
  - `scripts/verify-keycloak-e2e.ps1`: 실토큰 기반 권한 검증 자동화 스크립트 추가.
  - `SecurityConfig`: package 선언 복구 및 `@EnableMethodSecurity` 적용.
  - `TestSecurityConfig`: `/api/v1/admin/**`에 `ROLE_ADMIN` 접근 제어 및 메서드 보안 활성화.
  - `Book` Entity: 관리자 수정 API 지원을 위한 필드 갱신 로직(`update`) 추가.
  - `CatalogService`/`MemberService`/`OrderService`: 관리자용 생성/수정/삭제/강제취소 유스케이스 메서드 추가.
  - Member 및 Catalog 도메인의 계층 분리 (`ApiDelegateImpl` vs `Service`).
  - `GlobalExceptionHandler`에 `EntityNotFoundException` 핸들링 추가.
  - OpenAPI Generator Enum 생성 규칙을 `UPPERCASE`로 통일.
  - `GlobalExceptionHandler`에서 `BusinessException` 처리 시 `ErrorResponse` 매핑 표준화.
  - `SecurityConfig`에서 Swagger UI 관련 경로 허용 설정 추가.
  - `OrderService`에서 결제 실패 시 `PaymentFailedException`을 던지도록 수정.

## [0.1.0] - 2026-02-12
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
