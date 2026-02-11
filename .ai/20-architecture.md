# System Architecture

## 1. Architectural Style
- **Layered Architecture:** Presentation -> Application -> Domain -> Infrastructure 계층 구조를 따릅니다.
- **Modular Monolith:** 단일 배포 단위이지만, 내부적으로는 도메인별(Member, Catalog, Order, Delivery)로 모듈화하여 결합도를 낮춥니다.

## 2. Layer Description
### 2.1 Presentation Layer (`controller`, `dto`)
- REST API 엔드포인트를 제공합니다.
- 요청/응답 DTO 변환 및 기본적인 유효성 검사(Validation)를 수행합니다.

### 2.2 Application Layer (`service`, `facade`)
- 비즈니스 유스케이스를 조정(Orchestration)합니다.
- 트랜잭션 경계를 설정합니다.
- 도메인 객체 간의 협력을 유도합니다.

### 2.3 Domain Layer (`domain`, `repository interface`)
- 핵심 비즈니스 로직과 규칙을 포함합니다.
- Entity, Value Object, Domain Event 등을 정의합니다.
- 외부 기술에 의존하지 않는 순수한 POJO(Kotlin Class)를 지향합니다.

### 2.4 Infrastructure Layer (`infrastructure`)
- DB 구현체(JpaRepository), 외부 API 호출, 메시징 시스템 등 실제 기술적인 구현을 담당합니다.

## 3. Tech Components
- **Web Server:** Tomcat (Spring Boot Embedded)
- **Database:** H2 (Local/Test), PostgreSQL (Prod)
- **Migration:** Flyway (DB 스키마 버전 관리)
- **Security:** Spring Security + JWT (Stateless Authentication)

## 4. Package Structure
```
com.sc7258.shop
├── common          // 공통 유틸리티, 예외 처리
├── member          // 회원 도메인
├── catalog         // 도서/재고 도메인
├── order           // 주문/결제 도메인
└── delivery        // 배송 도메인
```
