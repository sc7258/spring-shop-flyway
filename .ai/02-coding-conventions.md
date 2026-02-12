# Coding Conventions

## Kotlin Style
- Follow the official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use `val` for read-only properties and `var` for mutable properties.
- Prefer expression bodies for functions with a single expression.

## Spring Boot Best Practices
- Use constructor injection for dependencies.
- Keep controllers thin; move business logic to services.
- Use DTOs (Data Transfer Objects) for API request and response bodies, avoiding exposing entities directly.
- Use standard HTTP status codes.

## OpenAPI Generator Rules
- **Source of Truth:** `openapi/openapi.yaml`
- **Generated Code:** `build/generate-resources` (또는 설정된 경로)에 생성된 코드는 **절대 직접 수정하지 않습니다.**
- **Layer Separation (Delegate Pattern):**
  - **Controller Layer (`ApiDelegateImpl`):**
    - OpenAPI Generator가 생성한 `ApiDelegate` 인터페이스를 구현합니다.
    - 예: `MemberApiDelegateImpl`
    - 역할: HTTP 요청/응답 처리, DTO 변환, `Service` 호출.
  - **Business Layer (`Service`):**
    - 순수 비즈니스 로직을 담당합니다.
    - 예: `MemberService`
    - 역할: 트랜잭션 관리, 도메인 로직 수행, Repository 호출.
    - **주의:** `Service`는 `ApiDelegate`를 직접 구현하지 않습니다.

### Configuration Options (Gradle)
`build.gradle.kts`의 `openApiGenerate` 태스크 설정 시 아래 옵션을 준수합니다:
```kotlin
configOptions.set(mapOf(
    "interfaceOnly" to "false",
    "delegatePattern" to "true",
    "useTags" to "true",
    "useSpringBoot3" to "true",
    "gradleBuildFile" to "false"
))
```

## Naming Conventions
- **Classes:** PascalCase (e.g., `ProductService`)
- **Functions/Variables:** camelCase (e.g., `findProductById`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages:** lowercase, no underscores (e.g., `com.sc7258.springshopflyway.domain`)

## Project Structure
- Organize code by feature or domain (e.g., `domain`, `controller`, `service`, `repository`).

## Versioning Strategy
- **Semantic Versioning (Major.Minor.Patch)**을 따릅니다.
- **Phase 완료 시** Minor 버전을 올립니다. (예: v0.1.0 -> v0.2.0)
- **버그 수정 시** Patch 버전을 올립니다. (예: v0.1.0 -> v0.1.1)
- **SNAPSHOT:** 개발 중인 버전에는 `-SNAPSHOT` 접미사를 붙입니다.
