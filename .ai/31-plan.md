# Phase 9 Implementation Plan: Performance & Optimization

> Rule:
> 이 문서는 현재 활성 Phase의 실행 계획만 관리합니다.
> 다음 Phase 이상의 작업을 섞지 않습니다.

**목표:** MariaDB 기반 운영 구조 위에서 조회 성능과 대용량 트래픽 대응 능력을 강화한다.

## 1. jOOQ Integration
### 1.1 Setup
- [ ] jOOQ 의존성과 코드 생성 환경을 현재 Gradle/MariaDB 설정에 맞춰 추가한다.
- [ ] 생성 코드를 기존 JPA 구조와 충돌 없이 병행 사용할 수 있도록 디렉토리/패키지 전략을 정한다.

### 1.2 Query Migration
- [ ] Catalog 검색 등 동적 조건이 많은 조회를 후보로 선정한다.
- [ ] 후보 조회를 jOOQ 기반으로 리팩토링하고 결과 일관성을 검증한다.

## 2. Performance Tuning
### 2.1 Data Access Optimization
- [ ] N+1 문제를 점검하고 fetch 전략 또는 쿼리 구조를 개선한다.
- [ ] 주요 검색/조회 경로에 필요한 인덱스를 정리한다.

### 2.2 Verification
- [ ] 성능 테스트 시나리오를 정의하고 기준 응답시간을 설정한다.
- [ ] 병목 지점 개선 전후를 비교해 문서화한다.

## 3. Documentation & Handoff
- [ ] `01-tech-stack.md`, `20-architecture.md`, `21-database-schema.md`, `30-roadmap.md`, `32-todo.md`, `33-changelog.md`를 성능 개선 결과에 맞게 갱신한다.
