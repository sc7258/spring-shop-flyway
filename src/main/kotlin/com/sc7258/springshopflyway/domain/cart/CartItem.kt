package com.sc7258.springshopflyway.domain.cart

import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "cart_items",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_cart_items_member_book", columnNames = ["member_id", "book_id"])
    ]
)
class CartItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Column(nullable = false)
    var quantity: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun increaseQuantity(amount: Int) {
        quantity += amount
        updatedAt = LocalDateTime.now()
    }

    fun changeQuantity(quantity: Int) {
        this.quantity = quantity
        updatedAt = LocalDateTime.now()
    }
}
