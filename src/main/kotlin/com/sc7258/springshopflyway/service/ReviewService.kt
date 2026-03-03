package com.sc7258.springshopflyway.service

import com.sc7258.springshopflyway.common.exception.EntityNotFoundException
import com.sc7258.springshopflyway.common.exception.ReviewAlreadyExistsException
import com.sc7258.springshopflyway.common.exception.ReviewNotAllowedException
import com.sc7258.springshopflyway.domain.catalog.BookRepository
import com.sc7258.springshopflyway.domain.member.MemberRepository
import com.sc7258.springshopflyway.domain.order.OrderRepository
import com.sc7258.springshopflyway.domain.order.OrderStatus
import com.sc7258.springshopflyway.domain.review.Review
import com.sc7258.springshopflyway.domain.review.ReviewRepository
import com.sc7258.springshopflyway.model.CreateReviewRequest
import com.sc7258.springshopflyway.model.ReviewResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val memberRepository: MemberRepository,
    private val bookRepository: BookRepository,
    private val orderRepository: OrderRepository
) {
    @Transactional
    fun createReview(email: String, bookId: Long, request: CreateReviewRequest): Long {
        val member = memberRepository.findByEmail(email)
            ?: throw EntityNotFoundException("Member not found: $email")
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException("Book not found: $bookId") }

        val hasPurchasedBook = orderRepository.existsByMemberEmailAndOrderItemsBookIdAndStatusNot(
            email,
            bookId,
            OrderStatus.CANCELLED
        )
        if (!hasPurchasedBook) {
            throw ReviewNotAllowedException("Only buyers can review this book")
        }
        if (reviewRepository.existsByMemberEmailAndBookId(email, bookId)) {
            throw ReviewAlreadyExistsException("Review already exists for book: $bookId")
        }

        val review = reviewRepository.save(
            Review(
                member = member,
                book = book,
                rating = request.rating,
                content = request.content
            )
        )
        return review.id!!
    }

    @Transactional(readOnly = true)
    fun getBookReviews(bookId: Long): List<ReviewResponse> {
        if (!bookRepository.existsById(bookId)) {
            throw EntityNotFoundException("Book not found: $bookId")
        }

        return reviewRepository.findAllByBookIdOrderByCreatedAtDesc(bookId).map { review ->
            ReviewResponse(
                id = review.id,
                bookId = review.book.id,
                memberName = review.member.name,
                rating = review.rating,
                content = review.content,
                createdAt = OffsetDateTime.of(review.createdAt, ZoneOffset.UTC)
            )
        }
    }
}
