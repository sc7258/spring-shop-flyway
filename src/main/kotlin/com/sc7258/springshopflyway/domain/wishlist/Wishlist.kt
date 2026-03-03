package com.sc7258.springshopflyway.domain.wishlist

import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "wishlists",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_wishlists_member_book", columnNames = ["member_id", "book_id"])
    ]
)
class Wishlist(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
