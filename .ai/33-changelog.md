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
- **Performance & Optimization:**
  - `build.gradle.kts`에 `spring-boot-starter-jooq`, `org.jooq.jooq-codegen-gradle`, `jooqCodegen` classpath를 추가해 Java 17 호환 jOOQ 3.19.x 코드 생성 환경을 구성.
  - Flyway 마이그레이션(`src/main/resources/db/migration/*.sql`)을 기준으로 jOOQ 메타모델을 생성하도록 DDLDatabase 설정 추가.
  - jOOQ 생성 코드를 `build/generated-src/jooq/main` / `com.sc7258.springshopflyway.jooq.generated`로 분리하고 `compileKotlin`에 코드 생성 태스크를 연결.
  - `BookCatalogQueryRepository`를 추가해 Catalog 목록/키워드 검색/페이징 조회를 jOOQ 기반으로 전환.
  - `CatalogControllerTest`에 정렬/검색/페이징 검증 케이스를 보강해 jOOQ 전환 이후 동작을 검증.
- **User Engagement Features:**
  - `CartApiDelegateImpl`, `CartService`, `CartItem`/`CartItemRepository` 추가로 장바구니 담기/수정/삭제/조회 API 구현.
  - `ReviewApiDelegateImpl`, `ReviewService`, `Review`/`ReviewRepository` 추가로 구매 사용자 리뷰 작성 및 공개 리뷰 목록 조회 API 구현.
  - `WishlistApiDelegateImpl`, `WishlistService`, `Wishlist`/`WishlistRepository` 추가로 위시리스트 추가/삭제/조회 API 구현.
  - `V4__create_phase7_tables.sql` 추가로 `cart_items`, `reviews`, `wishlists` 테이블 및 unique 제약 생성.
  - `CartControllerTest`, `ReviewControllerTest`, `WishlistControllerTest` 추가로 Phase 7 API 검증 범위 확장.
  - `openapi.yaml`에 Cart / Review / Wishlist 엔드포인트와 DTO 스키마 추가 및 OpenAPI 생성 코드 확장.
- **Security:**
  - `SecurityConfig`, `TestSecurityConfig`에서 `GET /api/v1/books/**`만 공개하고 리뷰 작성은 인증이 필요하도록 조정.
- **Database & Environment Alignment:**
  - `build.gradle.kts`에서 PostgreSQL 드라이버/Flyway 확장을 MariaDB 기준(`mariadb-java-client`, `flyway-mysql`)으로 전환.
  - `application.yml` 공통 설정과 `application-dev.yml`, `application-qa.yml`, `application-prod.yml`, `application-test.yml` 프로파일 파일을 분리.
  - `docker-compose.yml`을 MariaDB 개발 컨테이너 기준으로 재구성하고 `dev` 기본 프로필 전략을 도입.
  - `V1__init_schema.sql`, `V3__create_admin_audit_logs_table.sql`, `V4__create_phase7_tables.sql`의 PK DDL을 `AUTO_INCREMENT` 기준으로 정리해 MariaDB/H2 호환성 확보.
  - `ProfileConfigurationTest` 추가로 `qa` / `prod` 프로필 설정 로딩을 검증하고, `dev` 프로필은 실제 MariaDB 컨테이너 연결 및 부팅으로 확인.
- **Security & Administration:**
  - `AdminApiDelegateImpl`: 관리자 도서/회원/주문 관리 엔드포인트(`createBook`, `updateBook`, `deleteBook`, `deleteMember`, `cancelOrderAdmin`) 구현.
  - `AdminApiDelegateImpl`: `@PreAuthorize("hasRole('ADMIN')")` 및 `@AuditLog` 액션 확장 적용.
  - `AdminControllerTest`: 관리자 API 통합 시나리오(관리자 접근 성공, 일반 사용자 403, 관리자 CRUD/강제취소) 추가.
  - `LoginTokenIssuer` 도입: `/members/login` 토큰 발급을 Keycloak 위임(`KeycloakLoginTokenIssuer`)으로 전환하고 테스트 전용 스텁(`TestLoginTokenIssuer`) 추가.
  - Keycloak 로컬 Realm/Client/User 구성 후 `verify-keycloak-e2e.ps1` 실행으로 실토큰 인증/권한 검증 통과.
- **Documentation:**
  - `README.md`, `.ai/01-tech-stack.md`, `.ai/20-architecture.md`, `.ai/30-roadmap.md`, `.ai/31-plan.md`, `.ai/32-todo.md`: jOOQ 코드 생성 경로, 패키지, Phase 9 진행 상태를 반영.
  - `.ai/03-rules.md`, `.ai/README.md`, `.ai/commands/sync-status.md`: 현재 활성 Phase만 `31-plan.md`/`32-todo.md`에서 관리하도록 문서 경계 규칙 강화.
  - `30-roadmap.md` / `31-plan.md` / `32-todo.md`: `!sync` 기준으로 현재 활성 Phase를 `Phase 8 - Database & Environment Alignment`로 재정렬하고 세부 계획 재작성.
  - `README.md`, `docs/system-settings/README.md`, `docs/system-settings/mariadb-dev-setup.md`: MariaDB 기반 `dev` 실행 가이드와 `.env.local` 주의사항 반영.
  - `.env.example`, `.env.qa`, `README.md`, `.ai/01-tech-stack.md`, `.ai/20-architecture.md`: `.env.dev` / `.env.qa` / `.env.prod` 프로파일 정책과 `test` 비사용 정책을 명시.
  - `docs/trouble-shootings/spring-profile-and-dotenv-guide.md`: `application.yml`, `application-*.yml`, `.env`, `.env.*`의 실제 로딩 순서와 역할을 상세 정리.
  - `docs/system-settings/keycloak-live-verification.md`: Keycloak 실토큰 기반 인증/인가 점검 절차 추가.
  - `docs/trouble-shootings/swagger-auth-flow.md`: Swagger/OpenAPI 노출 구조와 OAuth(PKCE) 처리 방식, 주요 장애 대응 절차 정리.
  - `docs/trouble-shootings/spring-boot-swagger-static-openapi-guide.md`: 타 Spring Boot 프로젝트에 재사용 가능한 Swagger 정적 OpenAPI 구성 가이드 추가.
  - `docs/trouble-shootings/openapi-error-and-exception-handling-guide.md`: OpenAPI 에러 명세 관리와 코드 예외 처리 패턴을 타 프로젝트 재사용 관점으로 정리.
  - `docs/trouble-shootings/error-catalog-and-matrix-template.md`: 발생 가능 오류 사전 나열을 위한 에러 코드 카탈로그/API 매트릭스 템플릿 추가.
  - `30-roadmap.md` / `31-plan.md` / `32-todo.md`: Phase 6 진행상태 동기화 (`Admin API 완료`, `Keycloak 마이그레이션 정리/실토큰 검증 대기`).
  - `02-coding-conventions.md`: Exception Handling 규칙 추가.
  - `20-architecture.md`: Cross-Cutting Concerns (Exception Handling) 섹션 추가.
  - `openapi.yaml`: 에러 코드 테이블을 `<details>` 태그로 감싸서 가독성 개선.
- **Persistence Auditing:**
  - `JpaAuditingConfig`, `BaseTimeEntity` 추가로 `created_at`, `updated_at` 공통 audit 모델 도입.
  - `Member`, `Book`, `Order`, `OrderItem`, `Delivery`, `CartItem`, `Review`, `Wishlist` 엔티티를 JPA Auditing 기반으로 전환.
  - `JpaAuditingIntegrationTest` 추가로 insert/update 시각 자동 설정 및 갱신 동작 검증.
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
  - `SecurityConfig`, `TestSecurityConfig`에서 `WebSecurityCustomizer#ignoring` 사용을 제거하고 `authorizeHttpRequests(...).permitAll()`로 정리해 Spring Security 권장 방식으로 전환.
  - `WebConfig`에 `/swagger-ui.html`, `/swagger-ui/index.html` -> `/api/v1/swagger-ui...` 리다이렉트를 추가해 구형 Swagger 경로 접근 시 `No static resource swagger-ui/index.html` 오류를 방지.
  - `CatalogService.getBooks`의 조회 경로를 JPA Page 기반 구현에서 jOOQ 기반 구현으로 전환.
  - JPA 쓰기와 jOOQ 읽기를 동일 트랜잭션에서 사용할 때 결과가 어긋나지 않도록 `BookCatalogQueryRepository`에 flush 경계를 추가.
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
