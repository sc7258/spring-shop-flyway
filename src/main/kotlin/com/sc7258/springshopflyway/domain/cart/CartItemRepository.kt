package com.sc7258.springshopflyway.domain.cart

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = ["book"])
    fun findAllByMemberEmailOrderByCreatedAtAsc(email: String): List<CartItem>
    fun findByMemberEmailAndBookId(email: String, bookId: Long): CartItem?
}
