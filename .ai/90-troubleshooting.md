# Troubleshooting

이 문서는 프로젝트 진행 중 발생했던 주요 문제와 해결 과정을 기록합니다.

## 1. OpenAPI Generator Enum 매핑 문제

### 문제 현상
- `DeliveryControllerTest` 실행 시 `java.lang.IllegalArgumentException: No enum constant com.sc7258.springshopflyway.model.DeliveryResponse.Status.READY` 발생.
- `DeliveryService`에서 Entity(`Delivery`)의 `status`를 DTO(`DeliveryResponse`)로 변환하는 과정에서 에러 발생.

### 원인 분석
- **Entity Enum:** `domain/delivery/Delivery.kt`의 `DeliveryStatus`는 `READY`, `COMP`, `CANCEL` 등 대문자 상수를 가짐.
- **DTO Enum:** `openapi.yaml`에 정의된 `enum: [READY, COMP, CANCEL]`을 기반으로 OpenAPI Generator가 `model/DeliveryResponse.Status` Enum을 생성.
- **불일치:** `DeliveryService`에서 `DeliveryResponse.Status.valueOf(delivery.status.name)`을 호출할 때, `delivery.status.name`은 "READY"이지만, 생성된 `DeliveryResponse.Status` Enum에는 `READY` 상수가 없다고 판단됨. 이는 OpenAPI Generator의 기본 Enum 생성 전략이 `UPPERCASE`가 아니거나, 빌드 캐시 문제로 인해 발생할 수 있음.

### 해결 과정 및 최종 조치

#### 시도 1: `lowercase()` 변환 (실패)
- `openapi.yaml`의 enum 값을 소문자(`[ready, comp, cancel]`)로 변경하고, 서비스 코드에서 `delivery.status.name.lowercase()`를 사용하여 매핑 시도.
- 이 방법은 동작은 하지만, API 명세와 Entity의 Enum 명명 규칙이 달라져 일관성이 깨지는 문제가 있음.

#### 시도 2: `enumPropertyNaming` 옵션 설정 (성공)
- **`build.gradle.kts` 수정:**
  - `openApiGenerate` 태스크의 `configOptions`에 `"enumPropertyNaming" to "UPPERCASE"` 옵션을 추가.
  - 이 옵션은 OpenAPI Generator가 `enum` 값을 기반으로 Enum 상수를 생성할 때, 항상 **대문자(UPPERCASE)**로 만들도록 강제함.
  ```kotlin
  configOptions.set(mapOf(
      // ... 기존 옵션
      "enumPropertyNaming" to "UPPERCASE"
  ))
  ```

- **`openapi.yaml` 복구:**
  - `enum` 값을 다시 대문자(`[READY, COMP, CANCEL]`)로 복구하여 API 명세의 가독성을 유지.

- **`DeliveryService.kt` 복구:**
  - `lowercase()` 변환 로직을 제거하고, `DeliveryResponse.Status.valueOf(delivery.status.name)`을 그대로 사용하도록 수정.

### 결론
- OpenAPI Generator 사용 시, Entity와 DTO 간의 Enum 매핑이 깨지는 경우 `enumPropertyNaming` 옵션을 사용하여 생성 전략을 명시적으로 제어하는 것이 가장 깔끔하고 안정적인 해결책이다.
