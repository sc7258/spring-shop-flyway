# Phase 9 Implementation Plan: Performance & Optimization

> Rule:
> 이 문서는 현재 활성 Phase의 실행 계획만 관리합니다.
> 다음 Phase 이상의 작업을 섞지 않습니다.

**목표:** MariaDB 기반 운영 구조 위에서 조회 성능과 대용량 트래픽 대응 능력을 강화한다.

## 1. jOOQ Integration
### 1.1 Setup
- [x] jOOQ 의존성과 코드 생성 환경을 현재 Gradle/MariaDB 설정에 맞춰 추가한다.
- [x] 생성 코드를 기존 JPA 구조와 충돌 없이 병행 사용할 수 있도록 디렉토리/패키지 전략을 정한다.
- [x] Java 17 호환을 위해 jOOQ 3.19.x 기반으로 정렬한다.
- [x] Flyway SQL 스크립트 기준 DDLDatabase 코드 생성을 `jooqCodegen` 태스크로 검증한다.
- [x] 생성 경로를 `build/generated-src/jooq/main`, 패키지를 `com.sc7258.springshopflyway.jooq.generated`로 고정한다.

### 1.2 Query Migration
- [x] Catalog 목록/검색 조회를 첫 번째 jOOQ 이전 후보로 선정한다.
- [x] `CatalogService.getBooks`를 jOOQ 기반으로 리팩토링하고 페이징/키워드 검색/정렬 일관성을 검증한다.
- [x] JPA 쓰기와 jOOQ 읽기를 같은 트랜잭션에서 섞을 때 flush 경계를 추가해 결과 일관성을 보장한다.

## 2. Performance Tuning
### 2.1 Data Access Optimization
- [x] N+1 문제를 점검하고 Cart / Wishlist / Review 조회에 `@EntityGraph` 기반 최적화를 적용한다.
- [x] `V5__add_phase9_performance_indexes.sql`로 주요 검색/조회 경로 인덱스를 반영한다.

### 2.2 Verification
- [x] 성능 테스트 시나리오를 정의하고 기준 응답시간(SLO)을 설정한다.
- [x] Hibernate Statistics 기반 쿼리 수 비교(전/후)를 수행하고 결과를 문서화한다.

## 3. Documentation & Handoff
- [x] Catalog 조회 이전 결과와 jOOQ 운용 규칙을 `20-architecture.md`, `30-roadmap.md`, `32-todo.md`, `33-changelog.md`에 반영한다.
- [x] 성능 작업(N+1, 인덱스, 검증) 결과를 `21-database-schema.md`, `33-changelog.md`, `docs/trouble-shootings/phase9-performance-tuning-report.md`에 반영한다.
