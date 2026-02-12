package com.sc7258.springshopflyway.domain.order

import com.sc7258.springshopflyway.domain.catalog.Book
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_items")
class OrderItem(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Column(name = "order_price", nullable = false)
    val orderPrice: Int,

    @Column(nullable = false)
    val count: Int,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    var order: Order? = null

    init {
        book.removeStock(count)
    }

    fun cancel() {
        book.addStock(count)
    }
}
