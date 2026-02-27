# Spring Boot 공통 가이드: OpenAPI 에러 명세 + 예외 처리 패턴

이 문서는 다른 Spring Boot 프로젝트에서도 재사용할 수 있도록, 아래 두 가지를 한 번에 정리합니다.

- `openapi.yaml`에서 에러 계약(Error Contract) 정의하는 방법
- 코드에서 예외를 표준 에러 응답으로 변환하는 방법

## 1) 에러 계약을 먼저 고정한다

먼저 API 전체에서 공통으로 쓸 에러 응답 스키마를 정합니다.

```yaml
components:
  schemas:
    ErrorResponse:
      type: object
      properties:
        code:
          type: string
          example: "C001"
        message:
          type: string
          example: "Invalid Input Value"
        status:
          type: integer
          example: 400
```

권장 규칙:
- `code`: 서비스 내 고정 에러 코드 (예: `C001`, `M001`)
- `message`: 사용자/클라이언트가 읽을 수 있는 메시지
- `status`: HTTP Status와 동일한 숫자값

## 2) `openapi.yaml`에 에러 코드 테이블 유지

사람이 빠르게 이해할 수 있도록 `info.description`에 코드 테이블을 둡니다.

현재 프로젝트도 같은 방식으로 관리합니다:
- `openapi/openapi.yaml` 상단 `Error Codes` 테이블
- 각 API의 에러 응답에 `ErrorResponse`를 `$ref`로 공통 참조

예시:

```yaml
responses:
  '404':
    description: 주문 없음
    content:
      application/json:
        schema:
          $ref: '#/components/schemas/ErrorResponse'
```

## 3) 코드에서 예외를 일관되게 모델링한다

### 3-1) 중앙 에러 코드 Enum

에러 코드는 Enum 하나로 관리합니다.

예시 패턴:

```kotlin
enum class ErrorCode(
    val code: String,
    val message: String,
    val status: HttpStatus
)
```

현재 구현 위치:
- `src/main/kotlin/com/sc7258/springshopflyway/common/exception/ErrorCode.kt`

### 3-2) 비즈니스 예외 베이스 클래스

```kotlin
open class BusinessException(
    val errorCode: ErrorCode,
    message: String = errorCode.message
) : RuntimeException(message)
```

현재 구현 위치:
- `src/main/kotlin/com/sc7258/springshopflyway/common/exception/BusinessException.kt`

### 3-3) 도메인별 커스텀 예외

도메인에서 던질 예외는 `BusinessException`을 상속해 분리합니다.

현재 구현 위치:
- `src/main/kotlin/com/sc7258/springshopflyway/common/exception/CustomExceptions.kt`

## 4) `@RestControllerAdvice`에서 공통 변환

핵심은 모든 예외를 `ErrorResponse` 형식으로 변환하는 것입니다.

예시 패턴:

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            code = e.errorCode.code,
            message = e.message ?: e.errorCode.message
        )
        return ResponseEntity.status(e.errorCode.status).body(response)
    }
}
```

현재 구현 위치:
- `src/main/kotlin/com/sc7258/springshopflyway/common/exception/GlobalExceptionHandler.kt`

## 5) 다른 프로젝트에서 새 에러 추가 절차

1. `ErrorCode`에 코드/메시지/HTTP 상태 추가
2. 필요하면 커스텀 예외 클래스 추가
3. 서비스/도메인에서 해당 예외 throw
4. `openapi.yaml`의 Error Codes 테이블 업데이트
5. 해당 엔드포인트 `responses`에 `ErrorResponse` 연결
6. 컨트롤러 테스트에서 상태 코드 + `code`/`message` 검증

## 6) 스펙-코드 동기화 체크리스트 (중요)

아래 두 축이 항상 같아야 합니다.

- OpenAPI: `ErrorResponse` 스키마 필드 정의
- Runtime: `GlobalExceptionHandler`가 실제로 내려주는 필드

현재 프로젝트 기준 체크 포인트:
- OpenAPI 스키마에 `status` 필드가 정의되어 있음
- 핸들러에서는 `code`, `message` 중심으로 응답 구성

권장:
- `status`를 실제 응답에도 항상 채우거나
- 스키마에서 `status`를 optional로 명확히 문서화

## 7) 최소 검증 명령

```powershell
# OpenAPI의 ErrorResponse 스키마 확인
Invoke-WebRequest http://localhost:8080/api/v1/openapi.yaml

# 실제 에러 응답 확인 (예: 존재하지 않는 주문 조회/취소 API 호출)
# 응답 JSON의 code/message 형식이 스펙과 일치하는지 점검
```

