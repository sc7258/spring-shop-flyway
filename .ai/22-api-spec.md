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

### 2.3 내 정보 조회
- **GET** `/members/me`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
  ```json
  {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "USER",
    "city": "Seoul",
    "street": "Gangnam-gu",
    "zipcode": "12345"
  }
  ```

### 2.4 내 정보 수정
- **PUT** `/members/me`
- **Header:** `Authorization: Bearer {token}`
- **Request:**
  ```json
  {
    "name": "John Doe Updated",
    "city": "Seoul",
    "street": "Gangnam-daero",
    "zipcode": "06236"
  }
  ```
- **Response:** `200 OK` (`MemberProfileResponse`)
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

## 6. Cart API
### 6.1 장바구니 담기
- **POST** `/cart/items`
- **Header:** `Authorization: Bearer {token}`
- **Request:**
  ```json
  {
    "bookId": 1,
    "quantity": 2
  }
  ```
- **Response:** `201 Created`

### 6.2 장바구니 수량 수정
- **PUT** `/cart/items/{bookId}`
- **Header:** `Authorization: Bearer {token}`
- **Request:**
  ```json
  {
    "quantity": 3
  }
  ```
- **Response:** `200 OK`

### 6.3 장바구니 항목 삭제
- **DELETE** `/cart/items/{bookId}`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `204 No Content`

### 6.4 장바구니 조회
- **GET** `/cart`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
  ```json
  {
    "items": [
      {
        "bookId": 1,
        "title": "Spring Boot in Action",
        "quantity": 2,
        "unitPrice": 30000,
        "subtotal": 60000
      }
    ],
    "totalQuantity": 2,
    "totalAmount": 60000
  }
  ```

## 7. Review API
### 7.1 리뷰 작성
- **POST** `/books/{bookId}/reviews`
- **Header:** `Authorization: Bearer {token}`
- **Request:**
  ```json
  {
    "rating": 5,
    "content": "재구매하고 싶은 책입니다."
  }
  ```
- **Response:** `201 Created`

### 7.2 도서별 리뷰 목록 조회
- **GET** `/books/{bookId}/reviews`
- **Response:** `200 OK`
  ```json
  [
    {
      "id": 1,
      "bookId": 1,
      "memberName": "John Doe",
      "rating": 5,
      "content": "재구매하고 싶은 책입니다."
    }
  ]
  ```

## 8. Wishlist API
### 8.1 위시리스트 추가
- **PUT** `/wishlist/books/{bookId}`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `204 No Content`

### 8.2 위시리스트 삭제
- **DELETE** `/wishlist/books/{bookId}`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `204 No Content`

### 8.3 위시리스트 목록 조회
- **GET** `/wishlist`
- **Header:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
  ```json
  [
    {
      "bookId": 1,
      "title": "Spring Boot in Action",
      "author": "Craig Walls",
      "price": 30000
    }
  ]
  ```
