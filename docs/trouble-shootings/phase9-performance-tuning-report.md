# Phase 9 Performance Tuning Report

## 1. Scope
- N+1 완화: Cart / Wishlist / Review 조회 경로
- 인덱스 최적화: 주요 사용자 조회 및 주문 검증 경로
- 성능 검증: 쿼리 수 기반 비교 + 응답시간 목표(SLO) 정의

## 2. Query Optimization
### 2.1 N+1 개선 대상
- `CartService.getCart`
- `WishlistService.getWishlist`
- `ReviewService.getBookReviews`

### 2.2 적용 방식
- JPA `@EntityGraph`로 필요한 연관만 선로딩
  - `CartItemRepository.findAllByMemberEmailOrderByCreatedAtAsc` -> `book`
  - `WishlistRepository.findAllByMemberEmailOrderByCreatedAtDesc` -> `book`
  - `ReviewRepository.findAllByBookIdOrderByCreatedAtDesc` -> `member`
- `ReviewService.getBookReviews`는 `bookId`를 파라미터로 직접 사용해 불필요한 연관 접근을 제거

## 3. Index Optimization
Flyway migration: `V5__add_phase9_performance_indexes.sql`

- `idx_cart_items_member_created_at` on `cart_items(member_id, created_at)`
- `idx_wishlists_member_created_at` on `wishlists(member_id, created_at)`
- `idx_reviews_book_created_at` on `reviews(book_id, created_at)`
- `idx_orders_member_ordered_at` on `orders(member_id, ordered_at)`
- `idx_orders_member_status_id` on `orders(member_id, status, id)`
- `idx_order_items_book_order` on `order_items(book_id, order_id)`

## 4. Performance Verification
실행 명령:
```bash
./gradlew test --tests "com.sc7258.springshopflyway.performance.QueryPerformanceIntegrationTest"
```

쿼리 수 비교 결과 (2026-03-06):
- Cart 조회: `baseline=4` -> `optimized=1`
- Wishlist 조회: `baseline=4` -> `optimized=1`
- Review 조회: `baseline=4` -> `optimized=2`

## 5. Response Time Target (SLO)
로컬 dev(MariaDB) 기준 API 응답시간 목표:
- `GET /api/v1/books`: p95 <= 300ms
- `GET /api/v1/cart`: p95 <= 200ms
- `GET /api/v1/wishlist`: p95 <= 200ms
- `GET /api/v1/books/{bookId}/reviews`: p95 <= 250ms
- `GET /api/v1/orders`: p95 <= 300ms

## 6. Notes
- 본 리포트의 쿼리 수 검증은 통합테스트의 Hibernate Statistics 기반입니다.
- 응답시간 SLO는 추후 부하 도구(k6/JMeter 등)로 p95 측정을 자동화해 지속 검증합니다.
