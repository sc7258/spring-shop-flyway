# Feature Development Workflow

이 문서는 프로젝트에 새로운 기능을 추가할 때 따르는 표준 작업 절차(Workflow)를 정의합니다.
우리는 **API First** 설계와 **TDD(Test Driven Development)** 방법론을 지향하여, 명확한 스펙 정의와 안정적인 코드 구현을 목표로 합니다.

## 1. Workflow Overview

| 단계 | 활동 | 주요 산출물 |
|---|---|---|
| **Step 0** | **기획 및 문서화 (Planning)** | `10-prd.md`, `11-user-stories.md`, `30-roadmap.md` |
| **Step 1** | **API 명세 정의 (Contract First)** | `openapi/openapi.yaml`, Generated Interfaces |
| **Step 2** | **테스트 코드 작성 (Red)** | `*ControllerTest`, `*ServiceTest` (Fail) |
| **Step 3** | **비즈니스 로직 구현 (Green)** | Entity, Repository, Service Implementation |
| **Step 4** | **API 연결 (Delegate)** | `*ApiDelegateImpl` |
| **Step 5** | **리팩토링 및 문서화 (Refactor)** | Clean Code, Updated Docs |

---

## 2. 상세 가이드 (Step-by-Step)

### Step 0: 기획 및 문서화 (Planning)
기능 구현 전, 요구사항을 명확히 정의하고 계획을 수립합니다.
1. **PRD & User Stories:** `10-prd.md`와 `11-user-stories.md`에 새로운 기능의 목적, 사용자 가치, 인수 조건(Acceptance Criteria)을 정의합니다.
2. **Roadmap & Plan:** `30-roadmap.md`와 `31-plan.md`에 개발 일정과 우선순위를 반영합니다.
3. **Todo:** `32-todo.md`에 구체적인 작업 목록(Task)을 추가합니다.

### Step 1: API 명세 정의 (Contract First)
구현보다 인터페이스(약속)를 먼저 정의합니다.
1. **Spec 작성:** `openapi/openapi.yaml` 파일을 열어 새로운 경로(Path), 메서드(Method), 요청/응답 스키마(Schema)를 정의합니다.
2. **코드 생성:** `./gradlew openApiGenerate` 명령을 실행하여 `ApiDelegate` 인터페이스와 DTO 모델을 자동 생성합니다.
   - *Tip:* 생성된 코드는 `build/generated` 경로에 위치하며, 직접 수정하지 않습니다.

### Step 2: 테스트 코드 작성 (TDD - Red)
"어떻게 동작해야 하는지"를 먼저 검증 코드로 작성합니다.
1. **Controller Test:** `MockMvc`를 사용하여 API 호출 시 예상되는 HTTP 상태 코드와 응답 본문을 검증합니다.
   - 아직 구현체가 없으므로 404 또는 컴파일 에러가 발생해야 정상(Red)입니다.
2. **Service Test:** 비즈니스 로직의 성공/실패 케이스를 단위 테스트로 작성합니다.

### Step 3: 비즈니스 로직 구현 (Service Layer)
테스트를 통과하기 위한 핵심 로직을 구현합니다.
1. **Domain Model:** 필요한 `Entity`와 `Repository`를 생성합니다.
2. **Service Implementation:**
   - `Service` 클래스에 비즈니스 로직을 작성합니다.
   - 예외 상황에서는 `BusinessException` (예: `EntityNotFoundException`)을 명시적으로 던집니다.
   - 트랜잭션(`@Transactional`) 처리를 잊지 않습니다.

### Step 4: API 연결 (Delegate Implementation)
자동 생성된 인터페이스와 구현한 서비스를 연결합니다.
1. **DelegateImpl 구현:**
   - `*ApiDelegate` 인터페이스를 구현하는 클래스(`*ApiDelegateImpl`)를 작성합니다.
   - **역할:** HTTP 요청을 받아 `Service`를 호출하고, 결과를 `ResponseEntity`로 변환하여 반환합니다.
   - *주의:* 비즈니스 로직은 이곳에 작성하지 않고 `Service`로 위임합니다.

### Step 5: 리팩토링 및 문서화 (Refactor & Document)
코드를 다듬고 작업을 마무리합니다.
1. **Refactoring:** 중복 코드를 제거하고, 변수명을 명확하게 수정합니다. (기능 변경 없음)
2. **Documentation:**
   - `33-changelog.md`에 변경 사항을 기록합니다.
   - `32-todo.md`의 해당 항목을 완료 처리합니다.

#### **Technical Documentation Update (Critical for Recovery)**
이 문서들은 프로젝트의 **백업 및 복구(Recovery)**를 위한 핵심 자료이므로, 변경 시 반드시 업데이트해야 합니다.
- **Architecture (`20-architecture.md`):** 시스템 구조 변경 시 반드시 업데이트. (복구 시 참조)
- **Database Schema (`21-database-schema.md`):** Entity 변경 시 테이블 구조와 관계를 상세히 기술. (DDL 복구용)
- **API Spec (`22-api-spec.md`):** `openapi.yaml` 변경 사항을 요약하거나, 주요 스펙 변경을 기록. (API 복구용)

---

## 3. 예시: 장바구니(Cart) 기능 추가

1. **Plan:** `10-prd.md`에 장바구니 기능 정의 -> `32-todo.md`에 작업 추가.
2. **Spec:** `openapi.yaml`에 `POST /api/v1/cart/items` 정의.
3. **Test:** `CartControllerTest` 작성 -> `mockMvc.perform(post(...))` 실행 -> 404 확인.
4. **Service:** `CartService` 생성 -> `addItem()` 메서드 구현 -> `CartRepository` 생성.
5. **Delegate:** `CartApiDelegateImpl` 생성 -> `cartService.addItem()` 호출 연결.
6. **Done:** 테스트 전체 통과(Green) 확인 -> 커밋.
