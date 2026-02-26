# System Architecture

## 1. Architectural Style
- **Layered Architecture:** Presentation -> Application -> Domain -> Infrastructure 계층 구조를 따릅니다.
- **Modular Monolith:** 단일 배포 단위이지만, 내부적으로는 도메인별(Member, Catalog, Order, Delivery)로 모듈화하여 결합도를 낮춥니다.

## 2. Layer Description
### 2.1 Presentation Layer (`controller`, `dto`)
- REST API 엔드포인트를 제공합니다.
- 요청/응답 DTO 변환 및 기본적인 유효성 검사(Validation)를 수행합니다.

### 2.2 Application Layer (`service`, `facade`)
- 비즈니스 유스케이스를 조정(Orchestration)합니다.
- 트랜잭션 경계를 설정합니다.
- 도메인 객체 간의 협력을 유도합니다.

### 2.3 Domain Layer (`domain`, `repository interface`)
- 핵심 비즈니스 로직과 규칙을 포함합니다.
- Entity, Value Object, Domain Event 등을 정의합니다.
- 외부 기술에 의존하지 않는 순수한 POJO(Kotlin Class)를 지향합니다.

### 2.4 Infrastructure Layer (`infrastructure`)
- DB 구현체(JpaRepository), 외부 API 호출, 메시징 시스템 등 실제 기술적인 구현을 담당합니다.

## 3. Cross-Cutting Concerns (공통 관심사)
### 3.1 Exception Handling
- **Strategy:** 모든 예외는 `GlobalExceptionHandler`를 통해 중앙에서 처리합니다.
- **Structure:** `ErrorCode` Enum을 통해 에러 코드와 메시지를 관리하며, `BusinessException`을 상속받은 커스텀 예외를 사용합니다.
- **Response:** 클라이언트에게는 항상 표준화된 `ErrorResponse` 포맷(code, message, status)을 반환합니다.

### 3.2 Security & Authentication
- **Current (Phase 6):** Spring Security **OAuth2 Resource Server** 기반으로 보호 API 인증을 처리합니다.
  - `SecurityConfig`에서 JWT 인증 컨버터(`KeycloakJwtRolesConverter`)를 사용해 Keycloak Role을 `ROLE_*` 권한으로 매핑합니다.
  - `/api/v1/admin/**`는 `ROLE_ADMIN` 권한이 필요하며, Admin Delegate에는 `@PreAuthorize("hasRole('ADMIN')")`를 적용합니다.
- **Login Flow:** `/api/v1/members/login`은 `LoginTokenIssuer`를 통해 Keycloak 토큰 발급 엔드포인트로 위임합니다. 테스트 프로필은 `TestLoginTokenIssuer`로 스텁 토큰을 발급합니다.

### 3.3 Administration (Admin)
- **Role-Based Access Control (RBAC):** `ROLE_ADMIN` 권한을 가진 사용자만 접근 가능한 별도의 API 그룹(`/api/v1/admin/**`)을 운영합니다.
- **Audit Logging:** 관리자의 주요 활동(회원 차단, 주문 강제 취소 등)은 별도의 감사 로그(Audit Log)로 기록하여 추적성을 확보합니다.
- **AOP Implementation:** `@AuditLog` 어노테이션과 Aspect를 사용하여 비즈니스 로직 침투 없이 감사 로그를 남깁니다.

## 4. Tech Components
- **Web Server:** Tomcat (Spring Boot Embedded)
- **Database:** H2 (Local/Test), PostgreSQL (Prod)
- **Migration:** Flyway (DB 스키마 버전 관리)
- **Security:** Spring Security OAuth2 Resource Server (Keycloak JWT Role Mapping)

## 5. Package Structure
```
com.sc7258.shop
├── common          // 공통 유틸리티, 예외 처리
├── member          // 회원 도메인
├── catalog         // 도서/재고 도메인
├── order           // 주문/결제 도메인
└── delivery        // 배송 도메인
```
