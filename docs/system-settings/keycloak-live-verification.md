# Keycloak Live Verification Guide

이 문서는 Keycloak 로컬 설정(포트/재적용)과 실토큰 기반 인증/인가 검증 절차를 설명합니다.

## Local System Settings
- 애플리케이션 기본 포트: `8080`
- 로컬 Keycloak 호스트 포트: `9090`
- docker-compose 매핑: `9090:8080` (Host -> Container)

## 기존에 `docker compose up` 한 경우 (포트 변경 반영)
이미 컨테이너가 떠 있으면 포트 매핑 변경이 자동 반영되지 않습니다. 아래 순서로 재생성해야 합니다.

```powershell
docker compose stop keycloak
docker compose rm -f keycloak
docker compose up -d keycloak
docker ps --format "table {{.Names}}\t{{.Ports}}"
```

확인 시 `local-keycloak` 포트가 `0.0.0.0:9090->8080/tcp`로 보이면 정상입니다.

## Prerequisites
- 애플리케이션 실행 중
- Keycloak 서버 접근 가능
- 테스트용 계정 2개 준비
  - `ROLE_USER` 사용자 1명
  - `ROLE_ADMIN` 사용자 1명
- Keycloak Client 설정 확인 (클라이언트 분리)
  - API 서버용: `intellian-app-spring-client` (`confidential`)
  - Swagger UI용: `intellian-app-angular-client` (`public`)
    - `Standard Flow` 활성화
    - PKCE 허용
    - Client Authentication 비활성화(= secret 미사용)
  - Redirect URI 허용: `http://localhost:8080/swagger-ui/oauth2-redirect.html`
  - Web Origin 허용: `http://localhost:8080`
- 아래 환경변수 설정
  - `KEYCLOAK_AUTH_SERVER_URL`
  - `KEYCLOAK_CLIENT_SECRET`
  - `SWAGGER_OAUTH_CLIENT_ID` (기본값: `intellian-app-angular-client`)

## Swagger OAuth Login
Swagger UI에서 `Authorize` 버튼을 누르면 Keycloak 로그인 화면으로 이동하며, Google 연동이 설정된 경우 Google SSO 버튼을 통해 로그인할 수 있습니다.

## Verification Script
PowerShell에서 아래 명령을 실행합니다.

```powershell
.\scripts\verify-keycloak-e2e.ps1 `
  -AppBaseUrl "http://localhost:8080" `
  -KeycloakBaseUrl "http://localhost:9090" `
  -Realm "intellian-app" `
  -ClientId "intellian-app-spring-client" `
  -UserUsername "user@example.com" `
  -UserPassword "user-password" `
  -AdminUsername "admin@example.com" `
  -AdminPassword "admin-password"
```

## Expected Results
1. `ROLE_USER` 토큰으로 `/api/v1/orders` 호출 시 `401/403`이 아니어야 함
2. `ROLE_USER` 토큰으로 `/api/v1/admin/members` 호출 시 `403`
3. `ROLE_ADMIN` 토큰으로 `/api/v1/admin/members` 호출 시 `200`

모든 단계가 통과되면 실토큰 기반 인증/인가 검증이 완료됩니다.
