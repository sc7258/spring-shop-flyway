# Todo List

## Current Phase: Phase 5 - Advanced Features & Documentation

### 1. OpenAPI & Documentation (SSOT)
#### 1.1 Swagger UI 설정 개선
- [x] **Configuration**: `application.yml` 설정 (SpringDoc 비활성화, 정적 리소스 매핑).
- [x] **Path Change**: Swagger UI 경로를 `/api/v1/swagger-ui.html`로 변경.
- [x] **Security**: `SecurityConfig`에 Swagger UI 경로 허용 추가.

#### 1.2 Spec Refactoring
- [x] **Servers**: `openapi.yaml`의 `servers` URL을 상대 경로(`/api/v1`)로 변경.
- [x] **Error Codes**: 에러 코드 테이블을 `<details>` 태그로 감싸서 접을 수 있게 개선.
- [ ] **Error Handling**:
  - `ErrorCode` Enum 및 `BusinessException` 생성.
  - `LoginFailedException` 등 커스텀 예외 추가.
  - `GlobalExceptionHandler`에서 모든 예외를 `ErrorResponse`로 매핑.
  - `MemberApiDelegateImpl` 등에서 `try-catch` 제거 및 표준화.

### 2. jOOQ Integration
- [ ] **Setup**: jOOQ 설정 및 코드 생성 환경 구축.
- [ ] **Refactoring**: 복잡한 조회 쿼리(Catalog 검색 등)를 jOOQ로 리팩토링.

### 3. Performance
- [ ] **Optimization**: 쿼리 튜닝 및 인덱싱.

---
## Done
- [x] **Phase 4: Delivery & Integration Completed**
- [x] **Phase 3: Order Domain Completed**
- [x] **Phase 2: Catalog Domain Completed**
- [x] **Phase 1: Foundation & Member Domain Completed**
