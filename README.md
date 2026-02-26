# Spring Shop Flyway

온라인 서점 API 서비스 (아마존 클론)

## 시작하기 (Getting Started)

### 필수 요구사항 (Prerequisites)
- JDK 17 이상
- Docker 및 Docker Compose

### 데이터베이스 설정 (로컬 개발 환경)

1. **PostgreSQL 컨테이너 실행**
   ```bash
   docker-compose up -d
   ```

2. **데이터베이스 생성**
   ```bash
   # 컨테이너에 접속하여 'shopdb' 데이터베이스 생성
   docker exec -it local-postgres psql -U postgres -c "CREATE DATABASE shopdb;"
   ```

3. **연결 정보 확인**
   - 호스트: `localhost`
   - 포트: `5432`
   - 데이터베이스: `shopdb`
   - 사용자명: `postgres`
   - 비밀번호: `password`
   - 상세 가이드: `docs/system-settings/postgres-local-setup.md`

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
./gradlew bootRun
```
또는 빌드된 jar 파일을 직접 실행할 수도 있습니다:
```bash
java -jar build/libs/spring-shop-flyway-0.2.0.jar
```

### API 문서 (API Documentation)
- Swagger UI: [http://localhost:8080/api/v1/swagger-ui.html](http://localhost:8080/api/v1/swagger-ui.html)
- Swagger 처리 방식/트러블슈팅: `docs/trouble-shootings/swagger-auth-flow.md`
- Spring Boot 공통 설정 가이드: `docs/trouble-shootings/spring-boot-swagger-static-openapi-guide.md`
