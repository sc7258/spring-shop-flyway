# Tech Stack

## Language
- **Kotlin:** 1.9.25
- **Java:** 17

## Frameworks & Libraries
- **Spring Boot:** 3.x
- **Spring Data JPA:** For ORM and database access
- **Spring Web:** For building RESTful web services
- **Jackson:** For JSON processing (jackson-module-kotlin)
- **SpringDoc / Swagger UI:** API documentation visualization

## Database
- **H2:** Runtime dependency (likely for local development/testing)
- **PostgreSQL:** Runtime dependency (likely for production)
- **Flyway:** Database migration tool
- **jOOQ:** (Optional) For Type-safe SQL & Dynamic Query

## Build Tool
- **Gradle:** Kotlin DSL (build.gradle.kts)
- **OpenAPI Generator:** Code generation from OpenAPI spec (Gradle Plugin)

## Testing
- **JUnit 5:** (kotlin-test-junit5)
- **Spring Boot Starter Test:** Integration testing support
