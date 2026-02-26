# PostgreSQL Local Setup Guide

로컬 개발용 PostgreSQL 컨테이너 설정과 점검 절차를 정리한 문서입니다.

## 기본 정보
- 컨테이너명: `local-postgres`
- 호스트 포트: `5432`
- 기본 사용자: `postgres`
- 기본 비밀번호: `password`
- 기본 DB: `postgres` (애플리케이션용 `shopdb`는 별도 생성)

## 컨테이너 실행
```powershell
docker compose up -d postgres
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

`local-postgres`가 `0.0.0.0:5432->5432/tcp`로 보이면 정상입니다.

## 애플리케이션 DB 생성 (`shopdb`)
```powershell
docker exec -it local-postgres psql -U postgres -c "CREATE DATABASE shopdb;"
```

이미 존재하면 아래처럼 확인만 해도 됩니다.
```powershell
docker exec -it local-postgres psql -U postgres -tc "SELECT datname FROM pg_database WHERE datname='shopdb';"
```

## 연결 확인
```powershell
docker exec -it local-postgres psql -U postgres -d shopdb -c "SELECT now();"
```

## 자주 쓰는 운영 명령
```powershell
# 중지/시작
docker compose stop postgres
docker compose start postgres

# 로그 확인
docker logs --tail 200 local-postgres
```

## 주의
- `docker compose down -v`를 사용하면 `postgres_data` 볼륨까지 삭제되어 데이터가 초기화됩니다.
