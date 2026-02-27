# 오류 사전 나열 템플릿 (Error Catalog + API Error Matrix)

이 문서는 "발생 가능한 오류를 미리 최대한 빠짐없이 정리"하기 위한 실무 템플릿입니다.

## 1) 먼저 범주를 고정한다

아래 범주를 기준으로 누락을 줄입니다.

- 입력 검증 (`400`)
- 인증/인가 (`401`, `403`)
- 비즈니스 규칙 (`409`, `422`)
- 리소스 없음 (`404`)
- 외부 연동 실패 (`502`, `503`, `504`)
- 시스템/예상치 못한 오류 (`500`)
- 동시성/락/중복 요청 (`409`, `423`)

## 2) 에러 코드 카탈로그 템플릿

한 줄이 "하나의 계약"입니다. 코드/스펙/테스트가 모두 이 표를 기준으로 맞춰집니다.

| Domain | Error Code | HTTP | Message | Trigger Condition | Retry | Client Action | Log Level | Owner |
|---|---|---:|---|---|---|---|---|---|
| Common | C001 | 400 | Invalid Input Value | 요청 필드 검증 실패 | N | 입력 수정 후 재시도 | WARN | Backend |
| Common | C002 | 404 | Entity Not Found | ID 대상 리소스 없음 | N | 사용자에게 없음 안내 | INFO | Backend |
| Member | M001 | 409 | Email Already Exists | 중복 이메일 가입 | N | 다른 이메일 입력 | INFO | Backend |
| External | X001 | 503 | Upstream Service Unavailable | 외부 인증 서버 장애 | Y | 잠시 후 재시도 | ERROR | Platform |
| ... | ... | ... | ... | ... | ... | ... | ... | ... |

작성 규칙:
- `Error Code`는 절대 재사용하지 않습니다.
- `HTTP`와 `Retry` 정책은 반드시 함께 정의합니다.
- 사용자 행동(`Client Action`)을 적어두면 프론트/앱 대응이 쉬워집니다.

## 3) API 에러 매트릭스 템플릿

엔드포인트별로 실제로 발생 가능한 시나리오를 나열합니다.

| Endpoint | Scenario ID | Scenario | Precondition | Input/Trigger | Expected HTTP | Expected Code | Test Type |
|---|---|---|---|---|---:|---|---|
| `POST /api/v1/members/signup` | SIGNUP-01 | 이메일 형식 오류 | 없음 | `email=abc` | 400 | C001 | Controller Test |
| `POST /api/v1/members/signup` | SIGNUP-02 | 중복 이메일 | 기존 회원 존재 | 동일 이메일 가입 | 409 | M001 | Integration Test |
| `POST /api/v1/orders` | ORDER-01 | 재고 부족 | 재고 0 | 주문 생성 요청 | 409 | B001 | Service Test |
| `GET /api/v1/admin/members` | ADMIN-01 | 권한 없음 | USER 토큰 | 관리자 API 호출 | 403 | (보안 기본) | Security Test |
| ... | ... | ... | ... | ... | ... | ... | ... |

작성 규칙:
- `Scenario ID`는 테스트 메서드명과 같은 규칙으로 맞춥니다.
- 가능한 경우 `Expected Code`까지 고정합니다.
- 인증/인가 실패 케이스는 모든 보호 API에 최소 1개씩 둡니다.

## 4) 외부 의존성 실패 매트릭스 템플릿

DB, Keycloak, 외부 API 등 장애를 별도로 관리합니다.

| Dependency | Failure Mode | Detection | Expected API Result | Fallback | Alert |
|---|---|---|---|---|---|
| PostgreSQL | Connection timeout | DB health check 실패 | 503/500 | 없음 | On-call |
| Keycloak | Token introspection/JWK fetch 실패 | auth error 로그 | 401/503 | 재시도 | On-call |
| Payment API | 5xx | HTTP client exception | 502/503 | 취소/보상 트랜잭션 | On-call |
| ... | ... | ... | ... | ... | ... |

## 5) 테스트 커버리지 매핑 템플릿

오류가 문서에만 있고 테스트가 없으면 실제 운영에서 누락됩니다.

| Error Code | Required Tests | Current Test | Status | Gap |
|---|---|---|---|---|
| C001 | 입력 검증 실패 테스트 | `MemberControllerTest` | Done | - |
| M001 | 중복 가입 테스트 | `MemberControllerTest` | Done | - |
| B001 | 재고 부족 테스트 | `OrderControllerTest` | Done | - |
| X001 | 외부 서비스 장애 테스트 | 없음 | Missing | Mock/Stub 필요 |

## 6) 수집 자동화 체크 (초안)

코드에서 후보 오류를 빠르게 수집할 때 사용합니다.

```powershell
# 예외 throw/사용 지점
rg -n "throw|BusinessException|ErrorCode\\.|@ExceptionHandler" src/main/kotlin -S

# OpenAPI에서 ErrorResponse 참조 지점
rg -n "ErrorResponse|responses:|[\"']4\\d\\d[\"']|[\"']5\\d\\d[\"']" openapi/openapi.yaml -S

# 테스트에서 에러 코드 검증 지점
rg -n "code|message|status\\(|is4xx|is5xx" src/test/kotlin -S
```

## 7) 운영 반영 루프

새 오류가 운영에서 발견되면 아래 순서로 반드시 역반영합니다.

1. 에러 코드 카탈로그에 추가
2. OpenAPI Error Codes 표 + 해당 엔드포인트 responses 수정
3. `GlobalExceptionHandler`/예외 클래스 반영
4. 재현 테스트 추가
5. 릴리즈 노트 기록

## 8) Definition of Done

아래 조건을 모두 만족하면 "오류 사전 나열 완료"로 판단합니다.

- 모든 공개 API에 에러 매트릭스가 존재
- 모든 에러 코드가 OpenAPI와 코드에 동시에 존재
- 고위험 오류(인증/결제/외부연동/데이터 무결성)에 테스트 존재
- 최근 운영 이슈가 카탈로그에 역반영됨

