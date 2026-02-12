# Todo List

## Current Phase: Phase 2 - Catalog Domain

### 1. Catalog API Implementation
#### 1.1 도서 목록 조회 (Get Books)
- [ ] **Test (Red)**: `CatalogControllerTest` 작성 (목록 조회, 페이징, 검색).
- [ ] **Entity & Repository**: `Book` Entity (이미 존재), `BookRepository` 생성.
- [ ] **Service**: `CatalogApiDelegate` 구현체 `CatalogService` 작성.
- [ ] **QueryDSL Setup**: 동적 쿼리(검색)를 위한 QueryDSL 설정 (선택 사항, 필요시).

#### 1.2 도서 상세 조회 (Get Book Detail)
- [ ] **Test (Red)**: `CatalogControllerTest` 상세 조회 케이스 추가.
- [ ] **Service**: 상세 조회 로직 구현.
- [ ] **Exception**: 존재하지 않는 도서 조회 시 `EntityNotFoundException` 처리.

---
## Done
- [x] **Phase 1: Foundation & Member Domain Completed**
