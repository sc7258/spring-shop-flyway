# Glossary (용어 사전)

이 문서는 프로젝트에서 사용되는 주요 비즈니스 용어와 기술 용어를 정의하여, 팀원 간의 커뮤니케이션 혼선을 방지합니다.

## 1. 비즈니스 도메인 용어 (Business Domain)

### 회원 (Member)
- **회원 (Member):** 서비스에 가입하여 계정을 보유한 사용자.
- **게스트 (Guest):** 로그인하지 않은 상태로 서비스를 둘러보는 사용자.

### 도서 (Catalog)
- **ISBN:** 국제 표준 도서 번호. 도서를 식별하는 고유 키로 사용됨.
- **재고 (Stock):** 판매 가능한 도서의 수량. 주문 완료 시 차감됨.

### 주문 (Order)
- **장바구니 (Cart):** 구매하려는 도서를 임시로 담아두는 공간.
- **주문 (Order):** 사용자가 장바구니에 담긴 상품의 구매 의사를 확정한 상태.
- **주문 항목 (Order Item):** 주문에 포함된 개별 도서와 수량 정보.
- **결제 (Payment):** 주문에 대한 대금을 지불하는 행위.

### 배송 (Delivery)
- **배송지 (Shipping Address):** 상품이 배달될 실제 주소.
- **운송장 번호 (Tracking Number):** 택배사에서 발급하는 배송 추적 번호.

## 2. 기술 용어 (Technical Terms)

### Architecture
- **DTO (Data Transfer Object):** 계층 간 데이터 교환을 위해 사용하는 객체. 로직을 포함하지 않음.
- **Entity:** 데이터베이스 테이블과 매핑되는 도메인 객체. 식별자(ID)를 가짐.
- **Repository:** 데이터베이스 접근을 추상화한 계층 (Spring Data JPA Interface).

### Testing
- **Unit Test (단위 테스트):** 하나의 모듈(주로 Service, Domain)을 기준으로 독립적으로 검증하는 테스트.
- **Integration Test (통합 테스트):** 여러 모듈(Controller, Service, DB)이 상호작용하는 것을 검증하는 테스트.
- **Tombstone:** 이슈 관리 시, 파일 이동 후 이전 위치에 남겨두는 표식 파일.

### Infrastructure
- **Flyway:** 데이터베이스 스키마 버전을 관리하고 마이그레이션을 자동화하는 도구.
- **JWT (JSON Web Token):** 사용자 인증 정보를 안전하게 전달하기 위한 토큰 표준.
