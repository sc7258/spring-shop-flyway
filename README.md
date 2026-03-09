# Spring Shop Flyway

온라인 서점 API 서비스 (아마존 클론)

## 시작하기 (Getting Started)

### 필수 요구사항 (Prerequisites)
- JDK 17 이상
- Docker 및 Docker Compose

### 데이터베이스 설정 (Dev 환경)

1. **MariaDB 컨테이너 실행**
   ```bash
   docker-compose up -d
   ```

2. **연결 정보 확인**
   - 호스트: `localhost`
   - 포트: `3306`
   - 데이터베이스: `TEST_SAMPLE_DB`
   - 사용자명: `shop`
   - 비밀번호: `password`
   - 기본 프로필: `dev`
   - 상세 가이드: `docs/system-settings/mariadb-dev-setup.md`
   - `dev` 실행 시 `.env.dev`를 사용한다면 `DB_USERNAME=shop`, `DB_PASSWORD=password`로 맞춥니다.

### 로컬 Keycloak (선택)
로컬에서 Keycloak을 함께 사용할 경우, 애플리케이션(기본 `8080`)과 포트 충돌을 피하기 위해 **호스트 포트 `9090`**을 사용합니다.
- Keycloak URL (Local): `http://localhost:9090`
- Swagger OAuth Client ID(브라우저/Public): 기본값 `intellian-app-angular-client`
- 상세 가이드: `docs/system-settings/keycloak-live-verification.md`

### 빌드 (Build)
프로젝트를 빌드하고 테스트를 실행하려면 다음 명령어를 사용합니다:
```bash
./gradlew clean build
```
실행 가능한 jar 파일은 `build/libs/` 경로에 생성됩니다.

### jOOQ 코드 생성
Flyway 마이그레이션 스크립트를 기준으로 jOOQ 메타모델을 다시 생성하려면 다음 명령어를 사용합니다:
```bash
./gradlew jooqCodegen
```
- 생성 경로: `build/generated-src/jooq/main`
- 생성 패키지: `com.sc7258.springshopflyway.jooq.generated`
- `compileKotlin` 실행 시 `jooqCodegen`도 함께 수행됩니다.

### 테스트 실행 (Test)
`build` 외에도 테스트만 별도로 실행할 수 있습니다.

1. **전체 테스트 실행**
```bash
# macOS / Linux
./gradlew test

# Windows (PowerShell / CMD)
.\gradlew.bat test
```

2. **특정 테스트 클래스 실행**
```bash
# 예: AdminControllerTest만 실행
./gradlew test --tests "com.sc7258.springshopflyway.api.AdminControllerTest"
```

3. **실행된 테스트 상세 로그 확인**
```bash
# 캐시를 무시하고 실제 테스트를 다시 실행하며 상세 로그 출력
./gradlew test --rerun-tasks --info

# Windows (PowerShell / CMD)
.\gradlew.bat test --rerun-tasks --info
```

4. **테스트 리포트 확인**
- HTML 리포트: `build/reports/tests/test/index.html`

### 실행 (Run)
애플리케이션을 시작하려면 다음 명령어를 사용합니다:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
또는 빌드된 jar 파일을 직접 실행할 수도 있습니다:
```bash
java -jar build/libs/spring-shop-flyway-0.2.0.jar --spring.profiles.active=dev
```

### 실행 프로필 (Profiles)
- `dev`: 기본 프로필. 로컬 MariaDB와 개발용 Keycloak 설정을 사용합니다.
- `qa`: QA 환경용 MariaDB/Keycloak 주소를 사용합니다.
- `prod`: 운영 환경용 MariaDB/Keycloak 주소를 사용합니다.
- `test`: 자동 테스트 전용 프로필입니다.
- `.env` 파일 사용 시 프로필별로 `.env.dev`, `.env.qa`, `.env.prod`만 읽습니다.
- 파일명 `.env`와 `.env.local`은 현재 애플리케이션 로딩 대상이 아닙니다.

### 환경변수 파일 정책
- `dev`: `.env.dev`
- `qa`: `.env.qa`
- `prod`: `.env.prod`
- `test`: `.env` 파일을 읽지 않고 `application-test.yml`만 사용
- 시작점 예시는 `.env.example`을 복사해 프로필별 파일로 만드는 방식으로 관리합니다.

### API 문서 (API Documentation)
- Swagger UI: [http://localhost:8080/api/v1/swagger-ui.html](http://localhost:8080/api/v1/swagger-ui.html)
- Member Self API: `GET /api/v1/members/me`, `PUT /api/v1/members/me` (인증 필요)
- Swagger 처리 방식/트러블슈팅: `docs/trouble-shootings/swagger-auth-flow.md`
- Spring Boot 공통 설정 가이드: `docs/trouble-shootings/spring-boot-swagger-static-openapi-guide.md`
- OpenAPI 에러/예외 처리 공통 가이드: `docs/trouble-shootings/openapi-error-and-exception-handling-guide.md`
- 오류 사전 나열 템플릿: `docs/trouble-shootings/error-catalog-and-matrix-template.md`
- Phase 9 성능 최적화 리포트: `docs/trouble-shootings/phase9-performance-tuning-report.md`
