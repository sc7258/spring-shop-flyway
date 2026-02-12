# Phase 2 Implementation Plan: Catalog Domain

**목표:** 도서 정보를 관리하고 사용자가 검색할 수 있는 기능을 구현한다.

## 1. Catalog API Implementation
### 1.1 도서 목록 조회 (Get Books)
- [ ] **Test (Red)**
  - `CatalogControllerTest` 작성.
  - 페이징(Pageable) 및 검색 키워드(Keyword) 동작 검증.
- [ ] **Implementation (Green)**
  - `Book` Entity 확인 (V1 스키마에 이미 존재).
  - `BookRepository` 생성 (JpaRepository).
  - `CatalogService` (Delegate 구현체) 작성.
  - `BookListResponse` 매핑 로직 구현.
- [ ] **Refactoring**
  - 동적 쿼리 처리를 위한 QueryDSL 도입 검토 (또는 JPA Specification 사용).

### 1.2 도서 상세 조회 (Get Book Detail)
- [ ] **Test (Red)**
  - `CatalogControllerTest` 상세 조회 케이스 추가.
  - 존재하지 않는 ID 조회 시 404 에러 검증.
- [ ] **Implementation (Green)**
  - `findById` 로직 구현.
  - `EntityNotFoundException` 핸들링 (GlobalExceptionHandler).

## 2. Admin Features (Optional / Backlog)
- [ ] **도서 등록/수정 API**
  - 현재 API Spec에는 없으나, 테스트 데이터를 넣기 위해 필요할 수 있음.
  - `V2__insert_sample_books.sql`로 초기 데이터만 넣을지 결정 필요.

## 3. Verification
- [ ] **Integration Test**
  - 도서 목록 조회 -> 상세 조회 시나리오 테스트.
- [ ] **Swagger UI 확인**
  - Catalog API 동작 확인.
