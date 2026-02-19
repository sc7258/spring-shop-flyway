# AI Rules & Guidelines

이 파일은 AI 어시스턴트가 이 프로젝트에서 작업할 때 준수해야 할 핵심 규칙과 행동 지침을 정의합니다.

## 1. 기본 행동 지침 (General Behavior)
- **언어:** 모든 답변과 설명은 **한국어**로 작성합니다. (코드 내 주석은 영어를 권장하지만, 필요시 한글 병기 가능)
- **간결성:** 답변은 핵심 위주로 간결하게 작성하며, 불필요한 서론은 생략합니다.
- **맥락 파악:** 질문에 답변하기 전에 항상 프로젝트의 `tech-stack.md`와 `coding-conventions.md`를 먼저 고려합니다.

## 2. 작업 수행 및 보고 (Task Execution & Reporting)
- **Proactive Execution:** `32-todo.md`에 정의된 작업은 **사용자의 별도 승인 없이** 즉시 수행합니다. "할까요?" 같은 불필요한 질문을 줄이고, 주도적으로 진행합니다.
- **Batch Processing:** `32-todo.md`의 **하나의 소제목(예: `#### 1.1 주문 생성`)** 단위로 작업을 묶어서 일괄 처리합니다.
- **Auto Update:** 섹션 단위 작업이 완료되면, AI가 스스로 `32-todo.md`를 업데이트하고 결과를 요약하여 보고합니다.
- **Stop Condition:** 중간에 심각한 에러가 발생하거나, 설계 변경이 필요한 경우에만 작업을 멈추고 사용자에게 질문합니다.

## 3. 개발 워크플로우 (Development Workflow)
우리는 **API First** 설계와 **TDD(Test Driven Development)** 방법론을 지향하며, 다음의 6단계 프로세스를 엄격히 준수합니다.

### Step 0: 기획 및 문서화 (Planning) - *Critical*
기능 구현 전, 요구사항을 명확히 정의하고 계획을 수립합니다.
- **PRD & User Stories:** `10-prd.md`와 `11-user-stories.md`에 새로운 기능의 목적, 사용자 가치, 인수 조건(Acceptance Criteria)을 정의합니다.
- **Roadmap & Plan:** `30-roadmap.md`와 `31-plan.md`에 개발 일정과 우선순위를 반영합니다.
- **Todo:** `32-todo.md`에 구체적인 작업 목록(Task)을 추가합니다.

### Step 1: API 명세 정의 (Contract First)
구현보다 인터페이스(약속)를 먼저 정의합니다.
- **Spec 작성:** `openapi/openapi.yaml` 파일을 열어 새로운 경로(Path), 메서드(Method), 요청/응답 스키마(Schema)를 정의합니다.
- **코드 생성:** `./gradlew openApiGenerate` 명령을 실행하여 `ApiDelegate` 인터페이스와 DTO 모델을 자동 생성합니다.

### Step 2: 테스트 코드 작성 (TDD - Red)
"어떻게 동작해야 하는지"를 먼저 검증 코드로 작성합니다.
- **Controller Test:** `MockMvc`를 사용하여 API 호출 시 예상되는 HTTP 상태 코드와 응답 본문을 검증합니다.
- **Service Test:** 비즈니스 로직의 성공/실패 케이스를 단위 테스트로 작성합니다.

### Step 3: 비즈니스 로직 구현 (Service Layer)
테스트를 통과하기 위한 핵심 로직을 구현합니다.
- **Domain Model:** 필요한 `Entity`와 `Repository`를 생성합니다.
- **Service Implementation:** `Service` 클래스에 비즈니스 로직을 작성합니다. 예외 상황에서는 `BusinessException`을 명시적으로 던집니다.

### Step 4: API 연결 (Delegate Implementation)
자동 생성된 인터페이스와 구현한 서비스를 연결합니다.
- **DelegateImpl 구현:** `*ApiDelegate` 인터페이스를 구현하는 클래스(`*ApiDelegateImpl`)를 작성합니다.

### Step 5: 리팩토링 및 문서화 (Refactor & Document)
코드를 다듬고 작업을 마무리합니다.
- **Refactoring:** 중복 코드를 제거하고, 변수명을 명확하게 수정합니다. (기능 변경 없음)
- **Documentation:** `33-changelog.md`에 변경 사항을 기록하고, `32-todo.md`를 업데이트합니다.
- **Technical Docs Update (Critical for Recovery):**
  - **Architecture (`20-architecture.md`):** 시스템 구조 변경 시 반드시 업데이트.
  - **Database Schema (`21-database-schema.md`):** Entity 변경 시 테이블 구조와 관계를 상세히 기술.
  - **API Spec (`22-api-spec.md`):** `openapi.yaml` 변경 사항을 요약하거나, 주요 스펙 변경을 기록.

## 4. 코드 생성 규칙 (Code Generation)
- **파일 경로 명시:** 코드를 제안하거나 수정할 때, 해당 파일의 전체 경로를 명시합니다.
- **기존 스타일 준수:** 기존 코드의 들여쓰기, 네이밍 컨벤션, 패턴을 최대한 유지합니다.
- **주석:** 복잡한 로직이나 중요한 비즈니스 규칙에는 설명을 위한 주석을 추가합니다.
- **안전성:** Null Safety(Kotlin)를 철저히 지키고, 예외 처리를 적절히 수행합니다.

## 5. 기술 스택 준수 (Tech Stack Compliance)
- **라이브러리:** `tech-stack.md`에 명시되지 않은 외부 라이브러리 사용을 자제합니다. 꼭 필요한 경우 사용자에게 먼저 제안합니다.
- **버전 호환성:** Java 17 및 Spring Boot 3.x 버전에 호환되는 문법과 기능을 사용합니다.

## 6. 커밋 메시지 (Commit Messages)
- 커밋 메시지 제안 시 [Conventional Commits](https://www.conventionalcommits.org/) 규칙을 따릅니다.
  - 예: `feat: 사용자 로그인 기능 추가`, `fix: 결제 오류 수정`, `test: 회원가입 실패 테스트 케이스 추가`
