CREATE INDEX idx_cart_items_member_created_at
    ON cart_items (member_id, created_at);

CREATE INDEX idx_wishlists_member_created_at
    ON wishlists (member_id, created_at);

CREATE INDEX idx_reviews_book_created_at
    ON reviews (book_id, created_at);

CREATE INDEX idx_orders_member_ordered_at
    ON orders (member_id, ordered_at);

CREATE INDEX idx_orders_member_status_id
    ON orders (member_id, status, id);

CREATE INDEX idx_order_items_book_order
    ON order_items (book_id, order_id);
