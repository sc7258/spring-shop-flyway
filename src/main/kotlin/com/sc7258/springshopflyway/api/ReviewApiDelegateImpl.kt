package com.sc7258.springshopflyway.api

import com.sc7258.springshopflyway.model.CreateReviewRequest
import com.sc7258.springshopflyway.model.ReviewResponse
import com.sc7258.springshopflyway.service.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.net.URI

@Service
class ReviewApiDelegateImpl(
    private val reviewService: ReviewService
) : ReviewApiDelegate {

    override fun createReview(bookId: Long, createReviewRequest: CreateReviewRequest): ResponseEntity<Unit> {
        val reviewId = reviewService.createReview(getCurrentUserEmail(), bookId, createReviewRequest)
        return ResponseEntity.created(URI.create("/api/v1/books/$bookId/reviews/$reviewId")).build()
    }

    override fun getBookReviews(bookId: Long): ResponseEntity<List<ReviewResponse>> {
        return ResponseEntity.ok(reviewService.getBookReviews(bookId))
    }

    private fun getCurrentUserEmail(): String = SecurityContextHolder.getContext().authentication.name
}
