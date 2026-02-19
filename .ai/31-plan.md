# Phase 5 Implementation Plan: Stability & Monitoring

**목표:** 시스템의 안정성을 확보하고, 운영 가능한 수준의 모니터링 환경을 구축한다.

## 1. OpenAPI & Documentation (SSOT) - *Completed*
### 1.1 Swagger UI 설정 개선
- [x] **Configuration**: SpringDoc 비활성화, 정적 리소스 매핑.
- [x] **Path Change**: `/api/v1/swagger-ui.html`로 변경.
- [x] **Security**: `SecurityConfig`에 Swagger UI 경로 허용 추가.

### 1.2 Spec Refactoring & Quality
- [x] **Servers**: `openapi.yaml`의 `servers` URL을 상대 경로(`/api/v1`)로 변경.
- [x] **Error Codes**: 에러 코드 테이블을 `<details>` 태그로 감싸서 접을 수 있게 개선.
- [x] **Error Handling Standardization**:
  - `ErrorCode` Enum 및 `BusinessException` 생성.
  - `GlobalExceptionHandler`에서 모든 예외를 `ErrorResponse`로 매핑.
  - `ApiDelegateImpl`의 `try-catch` 제거 및 표준화.

## 2. Test Coverage Enhancement
### 2.1 Edge Case Testing
- [ ] **Order Domain**:
  - 재고 부족 시 주문 실패 테스트 (`OutOfStockException`).
  - 결제 실패 시 주문 상태 롤백 테스트 (`PaymentFailedException`).
  - 타인의 주문 취소 시도 시 권한 거부 테스트 (`InvalidInputException`).
- [ ] **Member Domain**:
  - 중복 이메일 가입 시도 테스트 (`DuplicateEmailException`).
  - 잘못된 비밀번호 로그인 시도 테스트 (`LoginFailedException`).

### 2.2 Integration Testing
- [ ] **Scenario Test**:
  - 회원가입 -> 로그인 -> 도서 검색 -> 장바구니(향후) -> 주문 -> 배송 조회까지의 전체 흐름 검증.

## 3. Monitoring & Logging
### 3.1 Actuator Setup
- [ ] **Dependency**: `spring-boot-starter-actuator` 추가.
- [ ] **Configuration**:
  - `management.endpoints.web.exposure.include=health,info,metrics` 설정.
  - `SecurityConfig`에서 Actuator 엔드포인트 접근 제어 (Admin 권한 필요 여부 검토).

### 3.2 Logging Strategy
- [ ] **Logback Config**:
  - `logback-spring.xml` 작성.
  - Console Appender (개발용) 및 File Appender (운영용) 분리.
  - JSON 포맷 로그 설정 (ELK 연동 대비).
- [ ] **Business Logging**:
  - `OrderService`: 주문 생성/취소 시 중요 정보(주문ID, 금액, 사용자ID) 로그 남기기.
  - `PaymentService`: 결제 승인/실패 로그 남기기.
  - `GlobalExceptionHandler`: 예외 발생 시 StackTrace와 함께 요청 정보 로그 남기기.

## 4. Verification
- [ ] **Final Check**:
  - `./gradlew test` 실행하여 모든 테스트 통과 확인.
  - `/actuator/health` 호출하여 시스템 상태 확인.
  - 로그 파일 생성 및 포맷 확인.
