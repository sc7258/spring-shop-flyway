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
- **Usage (Delegate Pattern):**
  - OpenAPI Generator가 `Api` 인터페이스, `ApiController`, `ApiDelegate` 인터페이스를 생성합니다.
  - 개발자는 `ApiDelegate` 인터페이스를 구현(`implements`)하는 `Service` 클래스(예: `MemberApiDelegateImpl`)를 작성합니다.
  - 이 구현체는 `@Service` 어노테이션을 붙여 스프링 빈으로 등록해야 합니다.

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
- **Packages:** lowercase, no underscores (e.g., `com.sc7258.shop.domain`)

## Project Structure
- Organize code by feature or domain (e.g., `domain`, `controller`, `service`, `repository`).
