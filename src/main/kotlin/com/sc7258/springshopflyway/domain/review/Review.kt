package com.sc7258.springshopflyway.domain.review

import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_reviews_member_book", columnNames = ["member_id", "book_id"])
    ]
)
class Review(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Column(nullable = false)
    val rating: Int,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
