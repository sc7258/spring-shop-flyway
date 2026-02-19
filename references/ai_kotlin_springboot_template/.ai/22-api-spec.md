# API Specification

## 1. Common
- **Base URL:** `/api/v1`
- **Content-Type:** `application/json`
- **Error Response:**
  ```json
  {
    "code": "ERROR_CODE",
    "message": "Error description",
    "status": 400
  }
  ```

## 2. [Domain 1] API
### 2.1 [기능 이름]
- **POST** `/resource`
- **Request:**
  ```json
  {
    "field": "value"
  }
  ```
- **Response:** `200 OK`

### 2.2 [기능 이름]
- **GET** `/resource/{id}`
