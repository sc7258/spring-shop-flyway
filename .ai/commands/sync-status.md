# Command: Sync Status

이 명령어는 프로젝트의 진행 상황을 문서 간에 동기화하고 정리하는 작업을 수행합니다.
사용자가 `@sync` 또는 `상태 동기화해줘`라고 요청하면 아래 절차를 따릅니다.

## Procedure

### 1. Check Todo (`.ai/32-todo.md`)
- `[x]` 표시된 완료 항목을 확인합니다.
- 완료된 항목 중 주요 마일스톤이나 기능 단위(Feature)가 있는지 식별합니다.

### 2. Update Changelog (`.ai/33-changelog.md`)
- 식별된 주요 완료 항목을 `Unreleased` 섹션 또는 새로운 버전(예: `v0.0.1`) 섹션에 추가합니다.
- 형식: `- Feat: 기능 설명` 또는 `- Chore: 작업 내용`

### 3. Update Plan (`.ai/31-plan.md`)
- `32-todo.md`에서 완료된 항목에 해당하는 `31-plan.md`의 계획을 `[x]`로 표시합니다.
- 만약 해당 Phase의 모든 계획이 완료되었다면, 다음 Phase를 활성화하거나 문서를 정리합니다.

### 4. Clean Todo (`.ai/32-todo.md`)
- 완료된 항목(`[x]`)을 `## Done` 섹션으로 이동시키거나, 너무 많으면 삭제하여 문서를 깔끔하게 유지합니다.
- 현재 진행 중인 작업(`[ ]`)만 상단에 남겨둡니다.

## Example Output
"상태 동기화를 완료했습니다.
- `32-todo.md`: 완료된 'Project Setup' 항목을 정리했습니다.
- `33-changelog.md`: '초기 프로젝트 설정 및 DB 연동' 내용을 추가했습니다.
- `31-plan.md`: 'Phase 1 - Setup' 단계를 완료 처리했습니다."
