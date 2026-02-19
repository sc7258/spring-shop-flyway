# Phase 6 Implementation Plan: Security & Administration

**목표:** 인증 체계를 고도화하고, 관리자 기능을 추가하여 운영 효율성을 높인다.

## 1. Keycloak Integration
### 1.1 Infrastructure Setup
- [ ] **Docker Compose**: Keycloak 컨테이너 추가 (`postgres` 의존성 확인).
- [ ] **Realm Configuration**: `shop-realm` 생성 및 클라이언트 설정 (`spring-shop-app`).
- [ ] **User & Role Setup**: 테스트용 사용자 및 `ROLE_ADMIN`, `ROLE_USER` 생성.

### 1.2 Spring Security Migration
- [ ] **Dependencies**: `spring-boot-starter-oauth2-resource-server` 추가.
- [ ] **Configuration**: `SecurityConfig`를 OAuth2 Resource Server 모드로 변경 (JWT Decoder 설정).
- [ ] **Token Provider Removal**: 기존 `JwtTokenProvider`, `JwtAuthenticationFilter` 제거 (또는 Deprecated 처리).

### 1.3 Verification
- [ ] **Login Test**: Keycloak을 통해 발급받은 토큰으로 API 호출 성공 확인.
- [ ] **Role Test**: `ROLE_USER` 권한으로 일반 API 접근 확인.

## 2. Admin API
### 2.1 Role-Based Access Control (RBAC)
- [ ] **Security Config**: `/api/v1/admin/**` 경로는 `ROLE_ADMIN`만 접근 가능하도록 설정.
- [ ] **Annotation**: `@PreAuthorize("hasRole('ADMIN')")` 적용 검토.

### 2.2 Admin Features Implementation
- [ ] **User Management**: 전체 회원 목록 조회, 특정 회원 강제 탈퇴 API.
- [ ] **Book Management**: 도서 등록/수정/삭제 API를 Admin 전용으로 이동/보완.
- [ ] **Order Management**: 전체 주문 목록 조회, 주문 강제 취소 API.

### 2.3 Audit Logging
- [ ] **Entity**: `AdminAuditLog` 엔티티 생성.
- [ ] **Service**: 관리자 액션 발생 시 로그 저장 로직 구현.
- [ ] **Aspect**: AOP를 활용하여 관리자 API 호출 시 자동으로 Audit Log 남기기 (선택 사항).

## 3. Final Verification
- [ ] **Integration Test**: 관리자 권한으로 API 호출 시나리오 테스트.
- [ ] **Access Denied Test**: 일반 사용자가 관리자 API 접근 시 403 Forbidden 확인.
