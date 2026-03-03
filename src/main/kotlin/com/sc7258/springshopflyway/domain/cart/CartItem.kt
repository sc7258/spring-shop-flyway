package com.sc7258.springshopflyway.domain.cart

import com.sc7258.springshopflyway.common.persistence.BaseTimeEntity
import com.sc7258.springshopflyway.domain.catalog.Book
import com.sc7258.springshopflyway.domain.member.Member
import jakarta.persistence.*

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
    val id: Long? = null
) : BaseTimeEntity() {
    fun increaseQuantity(amount: Int) {
        quantity += amount
    }

    fun changeQuantity(quantity: Int) {
        this.quantity = quantity
    }
}
