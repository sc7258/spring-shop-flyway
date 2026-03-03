# Project Roadmap

이 문서는 `spring-shop-flyway` 프로젝트의 장기적인 개발 계획과 주요 마일스톤을 정의합니다.
API First 및 TDD 방법론을 적용하여 단계적으로 기능을 확장합니다.

## Phase 1: Foundation & Member Domain (v0.1.0)
**목표:** 프로젝트 기본 환경을 구축하고, 사용자 인증/인가 시스템을 완성한다.
- [x] **Project Setup**
  - [x] Spring Boot 프로젝트 초기 설정 및 의존성 확인
  - [x] DB(H2, PostgreSQL) 및 Flyway 설정
  - [x] Swagger UI 정적 파일(`openapi.yml`) 제공 설정
  - [x] CI/CD 파이프라인 기초 (Build & Test)
- [x] **Member API**
  - [x] 회원가입 API (비밀번호 암호화)
  - [x] 로그인 API (JWT 발급)
  - [ ] 내 정보 조회 및 수정 API
  - [x] JWT 인증 필터 및 Security 설정

## Phase 2: Catalog Domain (v0.2.0)
**목표:** 상품 정보를 관리하고 사용자가 검색할 수 있는 기능을 구현한다.
- [x] **Catalog API**
  - [x] 도서 등록/수정/삭제 API (관리자용)
  - [x] 도서 목록 조회 API (페이징, 검색 기능)
  - [x] 도서 상세 조회 API
  - [x] 재고 관리 로직 (동시성 고려)

## Phase 3: Order Domain (v0.3.0)
**목표:** 사용자가 상품을 주문하고 결제하는 핵심 비즈니스 로직을 구현한다.
- [x] **Order API**
  - [x] 주문 생성 API (재고 차감 연동)
  - [x] 주문 목록 및 상세 조회 API
  - [x] 주문 취소 API (재고 복구)
- [x] **Payment Integration (Mock)**
  - [x] 결제 승인/취소 모의 구현
  - [x] 결제 상태에 따른 주문 상태 변경

## Phase 4: Delivery & Integration (v0.4.0)
**목표:** 배송 시스템을 연동하고, 전체 시스템의 안정성을 검증한다.
- [x] **Delivery API**
  - [x] 배송 생성 (주문 완료 시 자동 생성)
  - [x] 배송 상태 추적 API
- [x] **System Stability**
  - [x] 전 구간 통합 테스트 (E2E Test)

## Phase 5: Stability & Monitoring (v0.5.0)
**목표:** 시스템의 안정성을 확보하고, 운영 가능한 수준의 모니터링 환경을 구축한다.
- [x] **OpenAPI & Documentation (SSOT)**
  - [x] Swagger UI가 `openapi.yaml` 파일을 직접 로드하도록 설정 (SSOT 구축)
  - [x] Swagger UI 경로를 `/api/v1/swagger-ui.html`로 변경
  - [x] `servers` URL을 상대 경로(`/api/v1`)로 변경하여 환경 독립성 확보
  - [x] Error Handling Refactoring (표준 예외 처리)
- [x] **Test Coverage**
  - [x] 주요 비즈니스 로직(주문, 결제)의 엣지 케이스 테스트 보강
  - [x] 실패 시나리오(재고 부족, 결제 실패 등) 검증
- [x] **Monitoring & Logging**
  - [x] Spring Boot Actuator 설정 (Health Check, Metrics)
  - [x] Logback 설정 구체화 (File Appender, JSON Format)
  - [x] 주요 로직(주문, 결제)에 대한 구조화된 로그 남기기

## Phase 6: Security & Administration (v0.6.0) - Completed
**목표:** 인증 체계를 고도화하고, 관리자 기능을 추가하여 운영 효율성을 높인다.
- [x] **Keycloak Integration**
  - [x] Keycloak 컨테이너 설정 (Docker Compose)
  - [x] Spring Security OAuth2 Resource Server 설정
  - [x] 기존 JWT 인증 로직 완전 마이그레이션 (레거시 로그인 경로 정리 포함)
  - [x] Keycloak 실토큰 기반 인증/권한 검증
- [x] **Admin API**
  - [x] 관리자 권한(Role.ADMIN) 추가 및 접근 제어
  - [x] 관리자용 도서/회원/주문 관리 API 구현
  - [x] 관리자 API 접근/권한 검증 테스트 추가

## Phase 7: User Engagement Features (v0.7.0)
**목표:** 사용자의 참여를 유도하고 편의성을 높이는 기능을 추가한다.
- [x] **Cart (장바구니)**
  - [x] 장바구니 담기, 수정, 삭제 API
  - [x] 장바구니 조회 API
- [x] **Review (리뷰)**
  - [x] 도서 리뷰 작성 및 평점 부여 API
  - [x] 도서별 리뷰 목록 조회 API
- [x] **Wishlist (위시리스트)**
  - [x] 위시리스트 추가/삭제 API
  - [x] 위시리스트 목록 조회 API

## Phase 8: Database & Environment Alignment (v1.0.0)
**목표:** 사내 표준 환경에 맞춰 데이터베이스와 실행 프로파일 전략을 정비한다.
- [ ] **MariaDB Migration**
  - [ ] PostgreSQL 의존성과 로컬 실행 가이드를 MariaDB 기준으로 전환
  - [ ] Flyway 마이그레이션과 JPA 설정이 MariaDB에서 동일하게 동작하도록 검증
  - [ ] Docker Compose 및 개발용 DB 부트스트랩을 MariaDB 기준으로 재구성
- [ ] **Environment Profile Strategy**
  - [ ] `local` 프로필 제거
  - [ ] `dev`, `qa`, `prod` 3단계 프로필로 재구성
  - [ ] 환경별 datasource / logging / security 설정 차이를 문서화

## Phase 9: Performance & Optimization (v1.1.0)
**목표:** 대용량 트래픽 처리를 위한 성능 최적화 및 기술적 고도화를 수행한다.
- [ ] **jOOQ Integration**
  - [ ] jOOQ 설정 및 코드 생성 환경 구축
  - [ ] 복잡한 조회 쿼리(Catalog 검색 등)를 jOOQ로 리팩토링
- [ ] **Performance Tuning**
  - [ ] N+1 문제 해결 및 인덱싱 최적화
  - [ ] 성능 테스트 수행 및 병목 지점 개선

## Future Works (v1.2.0+)
- [ ] **Infrastructure & Architecture**
  - [ ] Redis 도입 (캐싱, 세션 관리)
  - [ ] MSA 전환 검토 (도메인별 서비스 분리)
  - [ ] ELK Stack 도입 (로그 모니터링)
  - [ ] Spring REST Docs 도입 검토 (테스트 기반 문서화)
