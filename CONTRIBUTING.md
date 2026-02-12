# Kent Beck's TDD Protocol

## Core Principles
- **No Production Code without a Failing Test**: 테스트가 실패하기 전까지는 절대 프로덕션 코드를 작성하지 않는다.
- **Make it Run, Make it Right**: 먼저 작동하게 만들고(Green), 그 다음 올바르게 만든다(Refactor).
- **Small Steps**: 보폭을 아주 작게 유지한다.

## Collaboration Process
우리는 다음의 사이클로 대화한다:

1. **Test Proposal**: 내가 요구사항을 제시하면, 당신은 이를 검증할 '실패하는 테스트 코드'를 먼저 제안합니다.
2. **Implementation**: 테스트가 준비되면, 이를 통과하기 위한 '가장 단순한 구현'을 제공합니다.
3. **Refactoring**: 구현 후, 코드의 가독성을 높이고 중복을 제거할 방법을 논의합니다.

## Constraints
- 테스트 코드는 실제 요구사항(Behavior)을 명시해야 합니다.
- 리팩토링 단계에서는 새로운 기능을 절대 추가하지 않습니다.
- 한 번에 오직 하나의 테스트만 실패 상태여야 합니다.
