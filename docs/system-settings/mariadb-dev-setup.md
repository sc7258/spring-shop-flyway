# MariaDB Dev Setup Guide

개발 환경(`dev` 프로필)에서 사용할 MariaDB 컨테이너 설정과 점검 절차를 정리한 문서입니다.

## 기본 정보
- 컨테이너명: `dev-mariadb`
- 호스트 포트: `3306`
- 데이터베이스: `TEST_SAMPLE_DB`
- 사용자: `shop`
- 비밀번호: `password`
- 활성 프로필: `dev`

## 컨테이너 실행
```powershell
docker compose up -d mariadb
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

`dev-mariadb`가 `0.0.0.0:3306->3306/tcp`로 보이면 정상입니다.

## 연결 확인
```powershell
docker exec -it dev-mariadb mariadb -ushop -ppassword TEST_SAMPLE_DB -e "SELECT NOW();"
```

## 애플리케이션 실행
```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

`.env.dev`를 사용한다면 아래 값도 함께 맞춥니다.
```dotenv
DB_USERNAME=shop
DB_PASSWORD=password
```

파일명은 `.env.dev`를 사용합니다.

## 자주 쓰는 운영 명령
```powershell
# 중지/시작
docker compose stop mariadb
docker compose start mariadb

# 로그 확인
docker logs --tail 200 dev-mariadb
```

## 주의
- `docker compose down -v`를 사용하면 `mariadb_data` 볼륨까지 삭제되어 데이터가 초기화됩니다.
- `qa`, `prod` 프로필은 로컬 컨테이너가 아니라 환경변수로 주입된 MariaDB 연결 정보를 사용합니다.
