# Spring Boot 공통 가이드: `openapi.yaml`로 Swagger UI 직접 표시하기

이 문서는 특정 프로젝트에 종속되지 않고, 다른 Spring Boot 프로젝트에도 그대로 적용할 수 있는 방식으로 정리한 가이드입니다.

## 목표

- OpenAPI 문서를 코드로 동적 생성하지 않고 `openapi.yaml`을 SSOT로 유지
- Swagger UI가 해당 YAML을 직접 읽어서 표시
- 필요 시 OAuth2 Authorization Code + PKCE까지 연동

## 1) 의존성 추가

Gradle:

```kotlin
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
}
```

Maven:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.3.0</version>
</dependency>
```

## 2) `openapi.yaml` 배치 전략 선택

옵션 A (권장, SSOT 분리):
- 저장: `openapi/openapi.yaml`
- 빌드 시 `src/main/resources/static/api/v1/openapi.yaml`로 복사

옵션 B (간단):
- 처음부터 `src/main/resources/static/api/v1/openapi.yaml`에 직접 저장

## 3) (옵션 A일 때) 빌드 복사 설정

Gradle 예시:

```kotlin
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from("$rootDir/openapi/openapi.yaml") {
        into("static/api/v1")
    }
}
```

Maven을 쓰는 프로젝트라면 같은 목적의 `resources`/`copy-resources` 단계로 `openapi/openapi.yaml`을 `target/classes/static/api/v1`로 복사하면 됩니다.

## 4) Spring Boot 설정 (`application.yml`)

```yaml
springdoc:
  swagger-ui:
    url: /api/v1/openapi.yaml
    path: /api/v1/swagger-ui.html
    disable-swagger-default-url: true
```

핵심:
- `url`은 Swagger UI가 실제 읽을 정적 YAML 경로
- `path`는 Swagger UI 접근 URL

## 5) Security 예외 경로 허용

Spring Security를 쓰면 Swagger 관련 경로를 인증 제외해야 UI가 정상 로드됩니다.

예시:

```kotlin
web.ignoring().requestMatchers(
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/v3/api-docs/**",
    "/api/v1/swagger-ui/**",
    "/api/v1/swagger-ui.html",
    "/api/v1/openapi.yaml"
)
```

## 6) OAuth2(PKCE)까지 연동할 때

`openapi.yaml`의 `components.securitySchemes`에 OAuth2 authorizationCode flow를 정의하고, UI 설정에 PKCE를 켭니다.

```yaml
springdoc:
  swagger-ui:
    oauth:
      client-id: ${SWAGGER_OAUTH_CLIENT_ID}
      use-pkce-with-authorization-code-grant: true
      scopes: openid,profile,email
```

권장:
- Swagger UI는 브라우저 앱이므로 Public Client 사용
- 가능하면 Swagger 전용 Client ID 분리
- `client_secret`는 Swagger UI에 넣지 않음

## 7) 동작 확인 체크리스트

```powershell
# 1) 정적 OpenAPI 접근 확인
Invoke-WebRequest http://localhost:8080/api/v1/openapi.yaml

# 2) Swagger 설정 확인
Invoke-RestMethod http://localhost:8080/v3/api-docs/swagger-config

# 3) Swagger UI 접속
start http://localhost:8080/api/v1/swagger-ui.html
```

## 8) 자주 막히는 지점

- UI가 기본 Petstore를 띄움:
  - `springdoc.swagger-ui.url` 누락/오타 확인
  - `disable-swagger-default-url: true` 적용 확인
- `404 /api/v1/openapi.yaml`:
  - 정적 리소스 복사 경로 또는 리소스 포함 여부 확인
- OAuth 로그인 후 redirect 오류:
  - IdP(Client)에서 redirect URI와 web origin 허용값 확인
- 설정 바꿨는데 화면이 이전 값:
  - 앱 재시작 후 브라우저 캐시 제거(또는 시크릿 창)로 재확인

