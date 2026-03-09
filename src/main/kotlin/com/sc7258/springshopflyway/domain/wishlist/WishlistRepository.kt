package com.sc7258.springshopflyway.domain.wishlist

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<Wishlist, Long> {
    fun existsByMemberEmailAndBookId(email: String, bookId: Long): Boolean
    @EntityGraph(attributePaths = ["book"])
    fun findAllByMemberEmailOrderByCreatedAtDesc(email: String): List<Wishlist>
    fun deleteByMemberEmailAndBookId(email: String, bookId: Long): Long
}
