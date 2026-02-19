# System Architecture

## 1. Architectural Style
- **Layered Architecture:** Presentation -> Application -> Domain -> Infrastructure 계층 구조를 따릅니다.
- **Modular Monolith:** 단일 배포 단위이지만, 내부적으로는 도메인별로 모듈화하여 결합도를 낮춥니다.

## 2. Layer Description
### 2.1 Presentation Layer (`controller`, `dto`)
- REST API 엔드포인트를 제공합니다.
- 요청/응답 DTO 변환 및 기본적인 유효성 검사(Validation)를 수행합니다.

### 2.2 Application Layer (`service`)
- 비즈니스 유스케이스를 조정(Orchestration)합니다.
- 트랜잭션 경계를 설정합니다.
- 도메인 객체 간의 협력을 유도합니다.

### 2.3 Domain Layer (`domain`, `repository interface`)
- 핵심 비즈니스 로직과 규칙을 포함합니다.
- Entity, Value Object, Domain Event 등을 정의합니다.
- 외부 기술에 의존하지 않는 순수한 POJO(Kotlin Class)를 지향합니다.

### 2.4 Infrastructure Layer (`infrastructure`)
- DB 구현체(JpaRepository), 외부 API 호출, 메시징 시스템 등 실제 기술적인 구현을 담당합니다.

## 3. Cross-Cutting Concerns (공통 관심사)
### 3.1 Exception Handling
- **Strategy:** 모든 예외는 `GlobalExceptionHandler`를 통해 중앙에서 처리합니다.
- **Structure:** `ErrorCode` Enum을 통해 에러 코드와 메시지를 관리하며, `BusinessException`을 상속받은 커스텀 예외를 사용합니다.

### 3.2 Security & Authentication
- **Current:** JWT 기반 인증 등을 사용합니다.

## 4. Tech Components
- **Web Server:** Tomcat (Spring Boot Embedded)
- **Database:** H2 (Local/Test), PostgreSQL (Prod)
- **Migration:** Flyway (DB 스키마 버전 관리)

## 5. Package Structure
```
[패키지 구조 예시]
```
