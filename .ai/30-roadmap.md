# Project Roadmap

이 문서는 `spring-shop-flyway` 프로젝트의 장기적인 개발 계획과 주요 마일스톤을 정의합니다.
API First 및 TDD 방법론을 적용하여 단계적으로 기능을 확장합니다.

## Phase 1: Foundation & Member Domain (v0.1.0)
**목표:** 프로젝트 기본 환경을 구축하고, 사용자 인증/인가 시스템을 완성한다.
- [ ] **Project Setup**
  - [ ] Spring Boot 프로젝트 초기 설정 및 의존성 확인
  - [ ] DB(H2, PostgreSQL) 및 Flyway 설정
  - [ ] Swagger UI 정적 파일(`openapi.yml`) 제공 설정
  - [ ] CI/CD 파이프라인 기초 (Build & Test)
- [ ] **Member API**
  - [ ] 회원가입 API (비밀번호 암호화)
  - [ ] 로그인 API (JWT 발급)
  - [ ] 내 정보 조회 및 수정 API
  - [ ] JWT 인증 필터 및 Security 설정

## Phase 2: Catalog Domain (v0.2.0)
**목표:** 상품 정보를 관리하고 사용자가 검색할 수 있는 기능을 구현한다.
- [ ] **Catalog API**
  - [ ] 도서 등록/수정/삭제 API (관리자용)
  - [ ] 도서 목록 조회 API (페이징, 검색 기능)
  - [ ] 도서 상세 조회 API
  - [ ] 재고 관리 로직 (동시성 고려)

## Phase 3: Order Domain (v0.3.0)
**목표:** 사용자가 상품을 주문하고 결제하는 핵심 비즈니스 로직을 구현한다.
- [ ] **Order API**
  - [ ] 주문 생성 API (재고 차감 연동)
  - [ ] 주문 목록 및 상세 조회 API
  - [ ] 주문 취소 API (재고 복구)
- [ ] **Payment Integration (Mock)**
  - [ ] 결제 승인/취소 모의 구현
  - [ ] 결제 상태에 따른 주문 상태 변경

## Phase 4: Delivery & Integration (v1.0.0)
**목표:** 배송 시스템을 연동하고, 전체 시스템의 안정성을 검증하여 정식 버전을 릴리즈한다.
- [ ] **Delivery API**
  - [ ] 배송 생성 (주문 완료 시 자동 생성)
  - [ ] 배송 상태 추적 API
- [ ] **System Stability**
  - [ ] 전 구간 통합 테스트 (E2E Test)
  - [ ] 성능 테스트 및 쿼리 최적화
  - [ ] API 문서화 (Swagger/RestDocs) 최종 점검

## Future Works (v1.1.0+)
- [ ] Redis 도입 (캐싱, 세션 관리)
- [ ] MSA 전환 검토 (도메인별 서비스 분리)
- [ ] ELK Stack 도입 (로그 모니터링)
