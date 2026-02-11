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

## 2. Member API
### 2.1 회원가입
- **POST** `/members/signup`
- **Request:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe",
    "city": "Seoul",
    "street": "Gangnam-gu",
    "zipcode": "12345"
  }
  ```
- **Response:** `201 Created` (Location Header: `/api/v1/members/{id}`)

### 2.2 로그인
- **POST** `/members/login`
- **Request:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response:** `200 OK`
  ```json
  {
    "accessToken": "eyJhbGciOiJIUz..."
  }
  ```

## 3. Catalog API
### 3.1 도서 목록 조회
- **GET** `/books`
- **Query Params:** `page=0`, `size=10`, `keyword=spring`
- **Response:** `200 OK`
  ```json
  {
    "content": [
      {
        "id": 1,
        "title": "Spring Boot in Action",
        "author": "Craig Walls",
        "price": 30000,
        "stockQuantity": 100
      }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
  ```

### 3.2 도서 상세 조회
- **GET** `/books/{bookId}`
- **Response:** `200 OK`

## 4. Order API
### 4.1 주문 생성
- **POST** `/orders`
- **Header:** `Authorization: Bearer {token}`
- **Request:**
  ```json
  {
    "orderItems": [
      {
        "bookId": 1,
        "count": 2
      },
      {
        "bookId": 3,
        "count": 1
      }
    ]
  }
  ```
- **Response:** `201 Created` (Location Header: `/api/v1/orders/{id}`)

### 4.2 주문 내역 조회
- **GET** `/orders`
- **Response:** `200 OK`

## 5. Delivery API
### 5.1 배송 상태 조회
- **GET** `/deliveries/{deliveryId}`
- **Response:** `200 OK`
  ```json
  {
    "id": 1,
    "status": "SHIPPED",
    "trackingNumber": "123-456-789"
  }
  ```
