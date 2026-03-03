package com.sc7258.springshopflyway.domain.review

import com.sc7258.springshopflyway.common.persistence.BaseTimeEntity
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*

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
    val id: Long? = null
) : BaseTimeEntity()
