# Phase 8 Implementation Plan: Database & Environment Alignment

> Rule:
> 이 문서는 현재 활성 Phase의 실행 계획만 관리합니다.
> 다음 Phase 이상의 작업을 섞지 않습니다.

**목표:** 사내 표준 환경에 맞춰 MariaDB 기반 데이터베이스 전략과 `dev` / `qa` / `prod` 실행 프로파일 구조를 정비한다.

## 1. MariaDB Migration
### 1.1 Dependency & Runtime Alignment
- [ ] `build.gradle.kts`의 PostgreSQL 런타임 의존성을 MariaDB 기준으로 전환한다.
- [ ] `docker-compose.yml`과 개발용 DB 실행 절차를 MariaDB 기준으로 정리한다.

### 1.2 Configuration Update
- [ ] `application.yml`의 기본 datasource / dialect / Flyway 설정을 MariaDB 기준으로 재구성한다.
- [ ] 테스트 환경(H2 유지 여부 포함)과 운영 환경 간 차이를 명시한다.

### 1.3 Verification
- [ ] Flyway 마이그레이션이 MariaDB에서 정상 적용되는지 검증한다.
- [ ] 애플리케이션 기동 및 주요 통합 테스트가 MariaDB 전환 후에도 통과하는지 확인한다.

## 2. Environment Profile Strategy
### 2.1 Profile Structure
- [ ] `local` 프로필을 제거한다.
- [ ] `dev`, `qa`, `prod` 3단계 프로필로 재구성한다.

### 2.2 Environment-Specific Settings
- [ ] 환경별 datasource, logging, security, swagger 노출 정책 차이를 정리한다.
- [ ] 기본 활성 프로필과 배포 시 주입해야 하는 환경변수를 명확히 정의한다.

### 2.3 Verification
- [ ] 각 프로필에서 필수 설정 누락 없이 기동 가능한지 점검한다.

## 3. Documentation & Handoff
- [ ] `README.md` 실행 가이드를 새 프로파일/DB 기준으로 갱신한다.
- [ ] `01-tech-stack.md`, `20-architecture.md`, `21-database-schema.md`, `30-roadmap.md`, `32-todo.md`, `33-changelog.md`를 변경 내용에 맞게 동기화한다.
