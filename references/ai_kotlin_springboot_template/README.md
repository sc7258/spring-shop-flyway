# [Project Name]

[프로젝트 한 줄 설명]

## 시작하기 (Getting Started)

### 필수 요구사항 (Prerequisites)
- JDK 17 이상
- Docker 및 Docker Compose

### 데이터베이스 설정 (로컬 개발 환경)

1. **DB 컨테이너 실행**
   ```bash
   docker-compose up -d
   ```

2. **데이터베이스 생성**
   ```bash
   # 필요한 경우 DB 생성 명령어
   ```

### 빌드 (Build)
```bash
./gradlew clean build
```

### 실행 (Run)
```bash
./gradlew bootRun
```

### API 문서 (API Documentation)
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)