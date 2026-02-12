# Spring Shop Flyway

Online Bookstore API Service (Amazon Clone)

## Getting Started

### Prerequisites
- JDK 17+
- Docker & Docker Compose

### Database Setup (Local Development)

1. **Start PostgreSQL Container**
   ```bash
   docker-compose up -d
   ```

2. **Create Database**
   ```bash
   # Connect to the container and create 'shopdb'
   docker exec -it dev-postgres psql -U postgres -c "CREATE DATABASE shopdb;"
   ```

3. **Verify Connection**
   - Host: `localhost`
   - Port: `5432`
   - Database: `shopdb`
   - Username: `postgres`
   - Password: `password`

### Build & Run
```bash
./gradlew bootRun
```

### API Documentation
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
