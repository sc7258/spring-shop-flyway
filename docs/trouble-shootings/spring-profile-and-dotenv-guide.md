# Spring Profile and Dotenv Guide

이 문서는 현재 프로젝트에서 `application.yml`, `application-*.yml`, `.env`, `.env.*` 파일이 어떤 역할을 가지며 어떤 순서로 적용되는지 설명합니다.

## 1. 결론 먼저

현재 프로젝트는 아래 정책으로 동작합니다.

- Spring 공통 설정은 `application.yml`에 둡니다.
- 프로필별 차이는 `application-dev.yml`, `application-qa.yml`, `application-prod.yml`, `application-test.yml`에 둡니다.
- 추가 환경변수 파일은 `dev`, `qa`, `prod`에서만 사용합니다.
- 파일명 `.env`와 `.env.local`은 현재 애플리케이션 로딩 대상이 아닙니다.
- `test` 프로필은 `.env` 파일을 읽지 않고 `application-test.yml`만 사용합니다.

## 2. 실제 로딩 규칙

### 2.1 Spring 설정 파일

Spring Boot는 기본적으로 다음 순서로 설정을 읽습니다.

1. `application.yml`
2. 활성 프로필에 대응하는 `application-{profile}.yml`

즉 활성 프로필이 `qa`면 아래 두 파일이 함께 적용됩니다.

1. `src/main/resources/application.yml`
2. `src/main/resources/application-qa.yml`

프로필 파일에 같은 키가 있으면 `application-qa.yml` 값이 `application.yml` 값을 덮어씁니다.

### 2.2 Dotenv 파일

이 프로젝트는 Spring 기본 기능만 쓰는 것이 아니라, 애플리케이션 시작 전에 Kotlin 코드로 `.env.*` 파일을 읽습니다.

관련 코드:
- `src/main/kotlin/com/sc7258/springshopflyway/SpringShopFlywayApplication.kt`

현재 분기 규칙은 아래와 같습니다.

- `dev` -> `.env.dev`
- `qa` -> `.env.qa`
- `prod` -> `.env.prod`
- 그 외 -> 로드 안 함

즉 파일명 `.env` 자체는 현재 사용하지 않습니다.

## 3. 프로필별 실제 조합

### 3.1 `dev`

사용되는 조합:

1. `application.yml`
2. `application-dev.yml`
3. `.env.dev`

역할:

- `application.yml`: 공통 설정
- `application-dev.yml`: Dev용 datasource, JPA dialect, Keycloak 주소
- `.env.dev`: 민감값 또는 환경별 세부 변수 (`DB_USERNAME`, `DB_PASSWORD`, `KEYCLOAK_CLIENT_SECRET` 등)

### 3.2 `qa`

사용되는 조합:

1. `application.yml`
2. `application-qa.yml`
3. `.env.qa`

역할:

- `application.yml`: 공통 설정
- `application-qa.yml`: QA용 datasource 기본 주소, JPA, Keycloak 주소
- `.env.qa`: QA 비밀값 및 접속 계정

### 3.3 `prod`

사용되는 조합:

1. `application.yml`
2. `application-prod.yml`
3. `.env.prod`

역할:

- `application.yml`: 공통 설정
- `application-prod.yml`: 운영용 datasource 기본 주소, JPA, Keycloak 주소
- `.env.prod`: 운영 비밀값 및 접속 계정

### 3.4 `test`

사용되는 조합:

1. `application.yml`
2. `application-test.yml`

특징:

- `.env.test`를 읽지 않습니다.
- `.env.dev`, `.env.qa`, `.env.prod`도 읽지 않습니다.
- 테스트는 외부 환경파일 의존성을 줄이고, `application-test.yml`만으로 닫히게 유지합니다.

## 4. 각 파일에 무엇을 두는가

### 4.1 `application.yml`

공통 설정을 둡니다.

예:

- `management.endpoints`
- `spring.application.name`
- `spring.profiles.default`
- `spring.flyway.locations`
- `springdoc.swagger-ui.*`
- 공통 Keycloak 설정 키 이름

원칙:

- 모든 환경에서 동일해야 하는 값만 둡니다.
- 환경마다 달라지는 DB 주소, 계정, 로그 상세도는 넣지 않습니다.

### 4.2 `application-*.yml`

환경별 차이를 둡니다.

예:

- `spring.datasource.url`
- `spring.jpa.database-platform`
- `spring.jpa.show-sql`
- `keycloak.auth-server-url`

원칙:

- 환경에 따라 달라지는 구조적 설정을 둡니다.
- 예: Dev는 `show-sql: true`, QA/Prod는 `false`

### 4.3 `.env.*`

환경별 변수값을 둡니다.

예:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `KEYCLOAK_CLIENT_SECRET`
- `KEYCLOAK_AUTH_SERVER_URL`
- `SWAGGER_OAUTH_CLIENT_ID`

원칙:

- 자주 바뀌는 값
- 비밀값
- 서버별 접속값

을 주로 둡니다.

## 5. 값이 최종적으로 어떻게 해석되는가

예를 들어 `application-dev.yml`에 아래가 있다고 가정합니다.

```yaml
spring:
  datasource:
    url: jdbc:mariadb://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:TEST_SAMPLE_DB}
```

그리고 `.env.dev`에 아래가 있다면:

```dotenv
DB_HOST=10.10.10.20
DB_PORT=3306
DB_NAME=TEST_SAMPLE_DB
```

최종적으로 Spring은 아래 값처럼 해석합니다.

```text
jdbc:mariadb://10.10.10.20:3306/TEST_SAMPLE_DB
```

만약 `.env.dev`에 값이 없으면 `${...:default}` 문법에 의해 기본값이 사용됩니다.

## 6. 우선순위 이해

이 프로젝트에서 실무적으로 이해하면 아래 순서로 보면 됩니다.

1. JVM system property / OS environment variable
2. `.env.{profile}`에서 애플리케이션 시작 시 주입한 system property
3. `application-{profile}.yml`
4. `application.yml`
5. `${VAR:default}`의 기본값

중요한 점:

- `.env.*` 값은 `SpringShopFlywayApplication.kt`에서 `System.setProperty(...)`로 주입되므로 Spring 입장에서는 system property처럼 동작합니다.
- 따라서 `.env.*` 값이 있으면 `application-*.yml` 안 `${...}` placeholder가 그 값을 사용합니다.

## 7. 왜 `.env`와 `.env.local`을 쓰지 않는가

현재 프로젝트는 `dev`, `qa`, `prod` 3단계 실행 환경을 명확히 분리하려고 합니다.

그래서 아래처럼 파일명을 환경과 1:1로 맞췄습니다.

- `.env.dev`
- `.env.qa`
- `.env.prod`

이 방식의 장점:

- 어떤 프로필이 어떤 파일을 읽는지 바로 알 수 있습니다.
- `qa`가 실수로 `dev`나 `local` 값을 읽는 혼선을 줄일 수 있습니다.

## 8. 자주 헷갈리는 포인트

### 8.1 `test`도 `application.yml`을 쓰는가?

쓴다.

- `application.yml`
- `application-test.yml`

이 두 파일을 함께 사용하고, 테스트용 파일이 공통 설정을 덮어씁니다.

### 8.2 `test`도 `.env.test`를 만들어야 하는가?

현재 프로젝트 기준으로는 필요 없습니다.

이유:

- 테스트는 외부 파일 의존성이 적을수록 안정적입니다.
- 현재 `application-test.yml`에 필요한 값이 이미 들어 있습니다.

### 8.3 `.env` 파일은 완전히 안 쓰는가?

그렇습니다.

정확히는:

- `.env`는 안 씀
- `.env.dev`, `.env.qa`, `.env.prod`는 씀

## 9. 권장 운영 방식

### 9.1 Dev

- `application.yml`
- `application-dev.yml`
- `.env.dev`

### 9.2 QA

- `application.yml`
- `application-qa.yml`
- `.env.qa`

### 9.3 Prod

- `application.yml`
- `application-prod.yml`
- `.env.prod`

### 9.4 Test

- `application.yml`
- `application-test.yml`

## 10. 관련 파일

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-qa.yml`
- `src/main/resources/application-prod.yml`
- `src/main/resources/application-test.yml`
- `src/main/kotlin/com/sc7258/springshopflyway/SpringShopFlywayApplication.kt`
- `.env.example`
- `.env.dev`
- `.env.qa`
- `.env.prod`
