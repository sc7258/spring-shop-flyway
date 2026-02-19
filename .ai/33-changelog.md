# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

### Added
- **Documentation:**
  - `02-coding-conventions.md`: Exception Handling 규칙 추가.
  - `20-architecture.md`: Cross-Cutting Concerns (Exception Handling) 섹션 추가.
  - `openapi.yaml`: 에러 코드 테이블을 `<details>` 태그로 감싸서 가독성 개선.
- **OpenAPI & Swagger:**
  - OpenAPI SSOT 구축 (정적 파일 로드, 경로 변경).
  - Swagger UI 경로를 `/api/v1/swagger-ui.html`로 변경.
  - `SecurityConfig` 및 `WebConfig` 설정 개선.
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
