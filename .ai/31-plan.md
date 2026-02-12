# Phase 5 Implementation Plan: Advanced Features & Documentation

**목표:** 기술적 완성도를 높이고, 문서를 보강하여 정식 버전(v1.0.0)을 릴리즈한다.

## 1. OpenAPI & Documentation (SSOT)
### 1.1 Swagger UI 설정 개선
- [x] **Configuration**
  - SpringDoc이 자동 생성하는 문서 비활성화.
  - `openapi.yaml` 파일을 정적 리소스로 서빙.
  - Swagger UI가 `openapi.yaml`을 로드하도록 `application.yml` 설정.
  - Swagger UI 경로를 `/api/v1/swagger-ui.html`로 변경.
  - `SecurityConfig`에 Swagger UI 경로 허용 추가.

### 1.2 Spec Refactoring & Quality
- [x] **Servers**
  - `servers` URL을 상대 경로(`/api/v1`)로 변경.
- [x] **Error Codes**
  - 에러 코드 테이블을 `<details>` 태그로 감싸서 접을 수 있게 개선.
- [ ] **Error Handling Standardization**
  - `LoginFailedException` 등 커스텀 예외 정의.
  - `GlobalExceptionHandler`에서 모든 예외를 `ErrorResponse`로 매핑.
  - `ApiDelegateImpl`의 `try-catch` 제거 및 표준화.

## 2. jOOQ Integration
### 2.1 Setup
- [ ] **Gradle Configuration**
  - jOOQ 플러그인 및 의존성 추가.
  - DB 스키마 기반 코드 생성(Codegen) 설정.
- [ ] **Spring Boot Config**
  - `JooqConfig` 클래스 작성.

### 2.2 Refactoring
- [ ] **Catalog Search**
  - `CatalogService`의 도서 검색 로직(동적 쿼리)을 jOOQ로 리팩토링.
  - 기존 JPA Specification 또는 Query Method 대체.

## 3. Performance & Optimization
### 3.1 Query Tuning
- [ ] **N+1 Problem Check**
  - 주요 API(`getOrders`, `getBooks`)의 쿼리 로그 분석.
  - `Fetch Join` 또는 `EntityGraph` 적용 필요성 검토.
- [ ] **Indexing**
  - 검색 조건(`title`, `author`) 및 조인 컬럼(`member_id`, `order_id`) 인덱스 확인.

## 4. Verification
- [ ] **Final Test**
  - 전체 테스트 슈트 실행 (`./gradlew test`).
  - Swagger UI를 통한 API 호출 테스트 (인증 포함).
