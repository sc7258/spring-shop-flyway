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
   docker exec -it dev-postgres psql -U postgres -c "CREATE DATABASE shopdb;"
   ```

3. **연결 정보 확인**
   - 호스트: `localhost`
   - 포트: `5432`
   - 데이터베이스: `shopdb`
   - 사용자명: `postgres`
   - 비밀번호: `password`

### 빌드 (Build)
프로젝트를 빌드하고 테스트를 실행하려면 다음 명령어를 사용합니다:
```bash
./gradlew clean build
```
실행 가능한 jar 파일은 `build/libs/` 경로에 생성됩니다.

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
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)