# Swagger 처리 방식 정리 (현재 기준)

이 문서는 현재 애플리케이션에서 Swagger UI가 어떤 방식으로 제공되고, OAuth 인증이 어떻게 연결되는지 정리합니다.

## 1) OpenAPI 문서 소스와 노출 경로

- SSOT 파일: `openapi/openapi.yaml`
- 빌드 시 복사: `build.gradle.kts`의 `processResources`에서 `static/api/v1/openapi.yaml`로 복사
- Swagger UI가 읽는 스펙 URL: `/api/v1/openapi.yaml`
- Swagger UI 경로: `/api/v1/swagger-ui.html`

관련 설정:
- `src/main/resources/application.yml`
  - `springdoc.swagger-ui.url: /api/v1/openapi.yaml`
  - `springdoc.swagger-ui.path: /api/v1/swagger-ui.html`
  - `springdoc.swagger-ui.disable-swagger-default-url: true`

## 2) Security 처리 방식

- Swagger 관련 경로는 인증 제외(permit) 처리되어 로그인 없이 UI 접근 가능
- 일반 API는 OAuth2 Resource Server(JWT)로 보호

관련 코드:
- `src/main/kotlin/com/sc7258/springshopflyway/config/SecurityConfig.kt`
  - `webSecurityCustomizer()`에서 Swagger 관련 경로 ignore
  - `filterChain()`에서 `/api/v1/admin/**`는 `ROLE_ADMIN` 필요

## 3) Swagger OAuth 로그인 방식

- OpenAPI security scheme: `authorizationCode + PKCE`
- Authorization/Token URL: Keycloak realm endpoint 사용
- Swagger UI는 브라우저용 Public Client 사용 (secret 미사용)

관련 설정:
- `openapi/openapi.yaml`의 `components.securitySchemes.bearerAuth`
- `src/main/resources/application.yml`
  - `springdoc.swagger-ui.oauth.client-id: ${swagger.oauth.client-id}`
  - `springdoc.swagger-ui.oauth.use-pkce-with-authorization-code-grant: true`
  - `springdoc.swagger-ui.oauth.scopes: openid,profile,email`
  - 기본 Client ID: `${SWAGGER_OAUTH_CLIENT_ID:intellian-app-angular-client}`

## 4) Keycloak Client 분리 원칙

- API 서버용(Client Credentials/Token 검증 관점): `intellian-app-spring-client` (confidential)
- Swagger UI용(브라우저 로그인): `intellian-app-angular-client` (public)

핵심:
- Swagger UI는 브라우저 동작이므로 `client_secret`를 사용하지 않는 구성이 맞습니다.

## 5) 점검 명령어

```powershell
# Swagger UI 설정 확인
Invoke-RestMethod http://localhost:8080/v3/api-docs/swagger-config

# 실제 로드되는 정적 OpenAPI 확인
Invoke-WebRequest http://localhost:8080/api/v1/openapi.yaml
```

## 6) 자주 발생하는 이슈

### 증상 A: Authorize 창에 여전히 `client_secret` 입력칸이 보임
- 원인 후보:
  - 서버 재시작 전 오래된 설정 노출
  - 브라우저 자동완성/캐시
- 조치:
  1. 앱 재시작 (`./gradlew bootRun` 또는 `.\gradlew.bat bootRun`)
  2. 강력 새로고침(`Ctrl+F5`) 또는 시크릿 창 접속
  3. `SWAGGER_OAUTH_CLIENT_ID` 환경변수 값 확인

### 증상 B: Keycloak 로그인 후 `invalid_redirect_uri`
- 조치:
  - Redirect URI 허용: `http://localhost:8080/swagger-ui/oauth2-redirect.html`
  - Web Origin 허용: `http://localhost:8080`

### 증상 C: 로그인 성공했는데 Admin API가 403
- 정상 가능성:
  - 토큰에 `ROLE_ADMIN`이 없으면 `/api/v1/admin/**`는 403
- 확인:
  - Keycloak에서 해당 사용자에 관리자 role 매핑 여부 확인

