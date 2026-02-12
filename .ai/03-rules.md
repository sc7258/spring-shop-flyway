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

## 3. 개발 방법론 (Methodology)
### 3.1 API First Design
- **우선순위:** 구현보다 API 명세(Spec)를 먼저 정의합니다.
- **Source of Truth:** `openapi/openapi.yaml` 파일이 API의 기준입니다.
- **프로세스:**
  1. `openapi/openapi.yaml`에 엔드포인트 및 스키마 정의 (에러 코드 포함).
  2. 이를 기반으로 Controller, DTO, ExceptionHandler 구현.
  3. Swagger UI를 통해 스펙 검증.

### 3.2 Kent Beck's TDD Protocol
우리는 켄트 벡의 TDD 원칙을 따르며, 다음의 사이클로 협업합니다.

#### Core Principles
- **No Production Code without a Failing Test:** 테스트가 실패하기 전까지는 절대 프로덕션 코드를 작성하지 않습니다.
- **Make it Run, Make it Right:** 먼저 작동하게 만들고(Green), 그 다음 올바르게 만듭니다(Refactor).
- **Small Steps:** 보폭을 아주 작게 유지합니다.

#### Collaboration Process
1. **Test Proposal:** 사용자가 요구사항을 제시하면, AI는 이를 검증할 **'실패하는 테스트 코드'**를 먼저 제안합니다.
2. **Implementation:** 테스트가 준비되면, 이를 통과하기 위한 **'가장 단순한 구현'**을 제공합니다.
3. **Refactoring:** 구현 후, 코드의 가독성을 높이고 중복을 제거할 방법을 논의합니다.

#### Constraints
- 테스트 코드는 실제 요구사항(Behavior)을 명시해야 합니다.
- 리팩토링 단계에서는 새로운 기능을 절대 추가하지 않습니다.
- 한 번에 오직 하나의 테스트만 실패 상태여야 합니다.

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
