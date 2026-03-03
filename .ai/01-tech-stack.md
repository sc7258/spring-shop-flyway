# Tech Stack

## Language
- **Kotlin:** 1.9.25
- **Java:** 17

## Frameworks & Libraries
- **Spring Boot:** 3.5.10
- **Spring Data JPA:** For ORM and database access
- **Spring Web:** For building RESTful web services
- **Jackson:** For JSON processing (jackson-module-kotlin)
- **SpringDoc / Swagger UI:** API documentation visualization

## Database
- **H2:** Runtime dependency (likely for local development/testing)
- **MariaDB:** Runtime dependency for `dev` / `qa` / `prod`
- **Flyway:** Database migration tool
- **jOOQ:** (Planned) For Type-safe SQL & Dynamic Query

## Build Tool
- **Gradle:** Kotlin DSL (build.gradle.kts)
- **OpenAPI Generator:** Code generation from OpenAPI spec (Gradle Plugin)

## Environment Configuration
- **Spring Profiles:** `dev`, `qa`, `prod`, `test`
- **Dotenv Files:** `dev -> .env.dev`, `qa -> .env.qa`, `prod -> .env.prod`
- **Test Policy:** `test` 프로필은 `.env` 파일을 사용하지 않고 `application-test.yml`로만 동작

## Testing
- **JUnit 5:** (kotlin-test-junit5)
- **Spring Boot Starter Test:** Integration testing support
