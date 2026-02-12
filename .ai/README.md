# AI 컨텍스트 가이드

이 디렉토리(`.ai`)는 AI 어시스턴트가 프로젝트를 이해하고 효율적으로 작업을 수행하기 위해 참조하는 문서들을 관리합니다.
파일 이름에 번호를 부여하여 정렬 및 관리를 용이하게 합니다.

## 컨텍스트 파일 목록

### 0x. 프로젝트 기본 정보 (Core Context)
*   **`00-project-info.md`**: 프로젝트 개요, 목적, 주요 디렉토리 구조 설명.
*   **`01-tech-stack.md`**: 사용 언어, 프레임워크, 라이브러리, 빌드 도구 및 버전 정보.
*   **`02-coding-conventions.md`**: 코드 스타일, 명명 규칙, 아키텍처 패턴, 모범 사례.
*   **`03-rules.md`**: AI 어시스턴트가 준수해야 할 행동 지침 및 규칙.
*   **`04-roles.md`**: 프로젝트 수행을 위한 역할(PM, 개발자, 아키텍트 등) 및 책임 정의.

### 1x. 기획 및 요구사항 (Requirements & Design)
*   **`10-prd.md`** (Product Requirements Document): 제품 요구사항 정의서. 기능 명세, 비즈니스 로직, 제약 사항 등.
*   **`11-user-stories.md`**: 사용자 관점의 기능 요구사항 및 인수 조건.
*   **`12-design-docs.md`**: UI/UX 디자인 가이드 또는 화면 설계 내용.

### 2x. 아키텍처 및 설계 (Architecture)
*   **`20-architecture.md`**: 시스템 아키텍처, 데이터 흐름, 컴포넌트 다이어그램.
*   **`21-database-schema.md`**: 데이터베이스 스키마(ERD), 테이블 구조, 관계 정의.
*   **`22-api-spec.md`**: API 엔드포인트 정의, 요청/응답 스펙.

### 3x. 작업 계획 및 진행 (Planning & Progress)
*   **`30-roadmap.md`**: 프로젝트의 장기적인 목표, 주요 마일스톤, 버전별 릴리즈 계획.
*   **`31-plan.md`**: 현재 단계의 구체적인 구현 계획, 작업 순서.
*   **`32-todo.md`** (또는 `tasks.md`): 세부 작업 목록 및 현재 상태.
*   **`33-changelog.md`**: 주요 변경 사항 기록.

### 9x. 기타 (Misc)
*   **`90-troubleshooting.md`**: 개발 중 발생한 주요 이슈 및 해결 방법 정리.
*   **`91-glossary.md`**: 프로젝트에서 사용하는 도메인 용어 정의.

---
**참고:** 위 파일들은 프로젝트의 규모와 필요에 따라 선택적으로 생성하여 관리합니다. AI에게 작업을 요청할 때 관련 파일을 함께 참조시키면 더 정확한 결과를 얻을 수 있습니다.

## 사용 문서 활용 방법 (Usage Instructions)

### 1. 에이전트 가동 절차 (Operational Protocol)
모든 질문에 답하기 전, AI는 다음 순서로 컨텍스트를 확인해야 합니다.

*   **Context Check**: 먼저 `.ai/` 폴더 내의 `01-tech-stack.md`와 `02-coding-conventions.md`를 읽고 프로젝트 성격을 파악한다.
*   **State Sync**: `31-plan.md`를 읽어 현재 작업의 위치와 다음 단계를 확인한다.
*   **Consistency**: 제안하는 코드가 정의된 API 명세(예: `api-spec.yaml` 등)를 위반하지 않는지 대조한다.

### 2. 증강 코딩 워크플로우 (Augmented Workflow)
본 프로젝트는 Kent Beck의 방식을 따라 **'생각의 동기화'**를 중시합니다.

*   **No Guessing**: 모호한 요구사항은 추측하여 코딩하지 말고 질문할 것.
*   **Plan First**: 코드를 짜기 전, 항상 `31-plan.md`에 작업 계획을 작성하거나 업데이트하고 개발자의 승인을 얻을 것.
*   **Small Steps**: 한 번에 너무 많은 클래스를 만들지 말고, 하나의 테스트를 통과시킬 수 있는 최소 단위로 진행할 것.

### 3. 도구 활용 지침 (Gemini Specifics)
*   **Refactoring**: "구조 정리(Tidy)" 요청 시에는 로직의 동작을 변경하지 말고 오직 가독성과 구조만 개선할 것.
*   **Error Handling**: 예외 처리 로직은 `02-coding-conventions.md`에 정의된 `GlobalExceptionHandler` 형식을 강제할 것.
*   **Test Generation**: 테스트 코드 작성 시 `MockMvc`를 기본으로 하며, DB 접근이 필요한 경우에만 `@DataJpaTest`를 제안할 것.

### 4. 금지 사항 (Constraints)
*   개발자의 명시적 요청 없이 기존의 API 명세 파일 내용을 변경하지 말 것.
*   비즈니스 로직에 `System.out.println()`을 사용하지 말 것 (반드시 로깅 라이브러리 활용).
