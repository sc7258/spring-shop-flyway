# Todo List

## Current Phase: Phase 6 - Security & Administration

### 1. Keycloak Integration
- [ ] **Setup**: Keycloak 컨테이너 설정 (Docker Compose).
- [ ] **Configuration**: Spring Security OAuth2 Resource Server 설정.
- [ ] **Migration**: 기존 JWT 인증 로직을 Keycloak으로 위임.

### 2. Admin API
- [ ] **Role Management**: `ROLE_ADMIN` 권한 추가 및 접근 제어.
- [ ] **Admin Features**: 관리자용 도서/회원/주문 관리 API 구현.
- [ ] **Audit Logging**: 관리자 활동 로그(Audit Log) 구현.

---
## Done
- [x] **Phase 5: Stability & Monitoring Completed**
  - **OpenAPI & Documentation (SSOT)**: Swagger UI 경로 변경, 에러 핸들링 표준화.
  - **Test Coverage**: Order/Member 도메인 엣지 케이스 테스트 추가 (재고, 결제, 중복 가입 등).
  - **Integration Testing**: E2E 시나리오 테스트 검증 (회원가입 -> 배송 조회).
  - **Monitoring**: Actuator 설정 (`health`, `info`, `metrics`), JSON 로깅 설정 (`logback-spring.xml`).
- [x] **Phase 4: Delivery & Integration Completed**
- [x] **Phase 3: Order Domain Completed**
- [x] **Phase 2: Catalog Domain Completed**
- [x] **Phase 1: Foundation & Member Domain Completed**