package com.sc7258.springshopflyway.domain.wishlist

import com.sc7258.springshopflyway.common.persistence.BaseTimeEntity
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*

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
    val id: Long? = null
) : BaseTimeEntity()
