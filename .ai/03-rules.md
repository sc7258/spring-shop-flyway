# AI Rules & Guidelines

이 파일은 AI 어시스턴트가 이 프로젝트에서 작업할 때 준수해야 할 핵심 규칙과 행동 지침을 정의합니다.

## 1. 기본 행동 지침 (General Behavior)
- **언어:** 모든 답변과 설명은 **한국어**로 작성합니다. (코드 내 주석은 영어를 권장하지만, 필요시 한글 병기 가능)
- **간결성:** 답변은 핵심 위주로 간결하게 작성하며, 불필요한 서론은 생략합니다.
- **맥락 파악:** 질문에 답변하기 전에 항상 프로젝트의 `tech-stack.md`와 `coding-conventions.md`를 먼저 고려합니다.

## 2. 개발 방법론 (Methodology)
### 2.1 API First Design
- **우선순위:** 구현보다 API 명세(Spec)를 먼저 정의합니다.
- **Source of Truth:** `openapi/openapi.yaml` 파일이 API의 기준입니다.
- **프로세스:**
  1. `openapi/openapi.yaml`에 엔드포인트 및 스키마 정의 (에러 코드 포함).
  2. 이를 기반으로 Controller, DTO, ExceptionHandler 구현.
  3. Swagger UI를 통해 스펙 검증.

### 2.2 TDD (Test Driven Development)
- **Red-Green-Refactor:**
  1.  **Red:** 실패하는 테스트 코드를 먼저 작성합니다. (기능 명세 역할)
  2.  **Green:** 테스트를 통과하기 위한 최소한의 코드를 구현합니다.
  3.  **Refactor:** 중복을 제거하고 구조를 개선합니다.
- **테스트 범위:**
  - **Unit Test:** 도메인 로직, 서비스 계층의 비즈니스 규칙 검증.
  - **Integration Test:** API 엔드포인트(Controller)와 DB 연동 검증 (`@SpringBootTest`, `MockMvc`).

## 3. 코드 생성 규칙 (Code Generation)
- **파일 경로 명시:** 코드를 제안하거나 수정할 때, 해당 파일의 전체 경로를 명시합니다.
- **기존 스타일 준수:** 기존 코드의 들여쓰기, 네이밍 컨벤션, 패턴을 최대한 유지합니다.
- **주석:** 복잡한 로직이나 중요한 비즈니스 규칙에는 설명을 위한 주석을 추가합니다.
- **안전성:** Null Safety(Kotlin)를 철저히 지키고, 예외 처리를 적절히 수행합니다.

## 4. 기술 스택 준수 (Tech Stack Compliance)
- **라이브러리:** `tech-stack.md`에 명시되지 않은 외부 라이브러리 사용을 자제합니다. 꼭 필요한 경우 사용자에게 먼저 제안합니다.
- **버전 호환성:** Java 17 및 Spring Boot 3.x 버전에 호환되는 문법과 기능을 사용합니다.

## 5. 커밋 메시지 (Commit Messages)
- 커밋 메시지 제안 시 [Conventional Commits](https://www.conventionalcommits.org/) 규칙을 따릅니다.
  - 예: `feat: 사용자 로그인 기능 추가`, `fix: 결제 오류 수정`, `test: 회원가입 실패 테스트 케이스 추가`
