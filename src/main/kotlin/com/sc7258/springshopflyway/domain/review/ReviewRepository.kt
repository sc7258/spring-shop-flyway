package com.sc7258.springshopflyway.domain.review

import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {
    fun existsByMemberEmailAndBookId(email: String, bookId: Long): Boolean
    fun findAllByBookIdOrderByCreatedAtDesc(bookId: Long): List<Review>
}
