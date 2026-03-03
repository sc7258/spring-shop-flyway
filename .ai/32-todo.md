# Todo List

> Rule:
> 이 문서는 `Current Phase` 1개만 관리합니다.
> 다음 Phase / Future Works / Backlog 항목을 여기에 추가하지 않습니다.
> 다음 단계 계획은 `30-roadmap.md`에 기록합니다.

## Current Phase: Phase 8 - Database & Environment Alignment

### 1. MariaDB Migration
- [ ] PostgreSQL 의존성과 실행 가이드를 MariaDB 기준으로 전환
- [ ] Flyway 마이그레이션과 JPA 설정을 MariaDB 기준으로 검증
- [ ] Docker Compose 및 개발용 DB 부트스트랩을 MariaDB 기준으로 재구성

### 2. Environment Profile Strategy
- [ ] `local` 프로필 제거
- [ ] `dev` / `qa` / `prod` 3단계 프로필 재구성
- [ ] 환경별 설정 차이와 운영 절차 문서화

---
## Done
- [x] **Phase 7 - User Engagement Features**
  - **Cart**: 장바구니 담기/수정/삭제/조회 API 구현 및 검증 완료.
  - **Review**: 구매 사용자 리뷰 작성 및 도서별 공개 리뷰 목록 조회 API 구현.
  - **Wishlist**: 위시리스트 추가/삭제/조회 API 구현 및 멱등성 검증 완료.
  - **Verification**: `CartControllerTest`, `ReviewControllerTest`, `WishlistControllerTest` 추가 및 전체 테스트 통과.
- [x] **Phase 6 - Keycloak Integration**
  - **Setup**: Keycloak 컨테이너 설정 (Docker Compose).
  - **Configuration**: Spring Security OAuth2 Resource Server 설정.
  - **Migration Cleanup**: `MemberService.login`의 레거시 JWT 발급 제거 및 `LoginTokenIssuer` 기반 전환.
  - **Verification**: Keycloak 실토큰 기반 로그인/권한 E2E 검증 완료 (`scripts/verify-keycloak-e2e.ps1`).
- [x] **Phase 6 - Admin API**
  - **Role Management**: `ROLE_ADMIN` 권한 추가 및 접근 제어.
  - **Admin Features**: 관리자용 도서/회원/주문 관리 API 구현.
  - **Audit Logging**: 관리자 활동 로그(Audit Log) 구현.
  - **Verification**: `AdminControllerTest`로 관리자 시나리오 및 403 접근 거부 검증.
- [x] **Phase 6: Security & Administration Completed**
- [x] **Phase 5: Stability & Monitoring Completed**
  - **OpenAPI & Documentation (SSOT)**: Swagger UI 경로 변경, 에러 핸들링 표준화.
  - **Test Coverage**: Order/Member 도메인 엣지 케이스 테스트 추가 (재고, 결제, 중복 가입 등).
  - **Integration Testing**: E2E 시나리오 테스트 검증 (회원가입 -> 배송 조회).
  - **Monitoring**: Actuator 설정 (`health`, `info`, `metrics`), JSON 로깅 설정 (`logback-spring.xml`).
- [x] **Phase 4: Delivery & Integration Completed**
- [x] **Phase 3: Order Domain Completed**
- [x] **Phase 2: Catalog Domain Completed**
- [x] **Phase 1: Foundation & Member Domain Completed**
